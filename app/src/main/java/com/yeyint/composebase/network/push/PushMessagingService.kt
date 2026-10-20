package com.yeyint.composebase.network.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.yeyint.composebase.R
import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.network.components.PushEventBus
import com.yeyint.composebase.network.response.LocalizedText
import com.yeyint.composebase.network.response.PushPayload
import com.yeyint.composebase.network.response.forLanguage
import com.yeyint.composebase.ui.app.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Receives token refreshes and incoming pushes. The backend is expected to send data-only messages
 * (no `notification` block): that is the only way [onMessageReceived] runs while the app is in the
 * foreground — a `notification` block lets the OS draw it directly and skips this callback, which
 * is the wrong trade for anything the user needs to see the instant it happens.
 *
 * Needs, per project, outside this file: the Google Services plugin and a `google-services.json`
 * (see docs/CLONING.md) — without them Firebase never initialises and no push arrives.
 *
 * The tap intent carries [EXTRA_TYPE]/[EXTRA_ENTITY_ID]/[EXTRA_NOTIFICATION_ID] to
 * [MainActivity]; routing them to a screen is the app's own job, in its `onCreate`/`onNewIntent`.
 */
@AndroidEntryPoint
class PushMessagingService : FirebaseMessagingService() {

    @Inject lateinit var myPreference: MyPreference
    @Inject lateinit var gson: Gson
    @Inject lateinit var pushEventBus: PushEventBus

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        myPreference.saveFcmToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        if (data.isEmpty()) return
        val rawTitle = data["title"] ?: return
        val rawBody = data["body"]
        val language = myPreference.load(MyPreference.KEYS.LOCALE).orEmpty()

        // Both fields arrive JSON-stringified rather than as plain strings — parse, then localize.
        // A payload that doesn't parse falls back to the raw string rather than a blank
        // notification: garbled, but never silent.
        val title = runCatching { gson.fromJson(rawTitle, LocalizedText::class.java) }
            .getOrNull()
            ?.forLanguage(language)
            ?: rawTitle
        val payload = rawBody?.let { runCatching { gson.fromJson(it, PushPayload::class.java) }.getOrNull() }
        val body = payload?.description?.forLanguage(language) ?: rawBody.orEmpty()

        // Keyed by the entity the push is about when there is one — see trayNotificationIdFor.
        val trayId = payload?.entityId?.let(::trayNotificationIdFor)
            ?: payload?.notificationId?.hashCode()
            ?: System.currentTimeMillis().toInt()

        show(title, body, trayId, payload)
        // Any push is news to an open screen — a badge count, a list.
        pushEventBus.emit(payload?.type)
    }

    private fun show(title: String, body: String, trayId: Int, payload: PushPayload?) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Creating an existing channel is a no-op, so it is safe to do on every push.
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TYPE, payload?.type)
            putExtra(EXTRA_ENTITY_ID, payload?.entityId)
            putExtra(EXTRA_NOTIFICATION_ID, payload?.notificationId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, trayId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.notification_accent))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(trayId, notification)
    }

    companion object {
        const val CHANNEL_ID = "DEFAULT_CHANNEL"
        const val EXTRA_TYPE = "notification_type"
        const val EXTRA_ENTITY_ID = "notification_entity_id"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }
}
