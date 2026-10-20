package com.yeyint.composebase.network.response

import com.google.gson.annotations.SerializedName

/**
 * Shape of the FCM `data.body` field once parsed — this backend family sends it JSON-stringified
 * (`JSON.stringify({ description, type, notification_id, entity_id })`), the same as `data.title`
 * being a stringified [LocalizedText].
 *
 * [type] says what kind of push it is; [notificationId] is the backend's id for the in-app
 * notification (what a "mark as read" call takes); [entityId] is the thing the push is about — an
 * order, a booking, a chat — and is what groups pushes in the tray (see [com.yeyint.composebase.network.push.trayNotificationIdFor]).
 * Lives in `network.response` with the other DTOs so the R8 keep rule for them covers it. Rename [entityId]'s wire name to whatever the new backend calls it (the rider app's is
 * `order_id`).
 */
data class PushPayload(
    @SerializedName("description") val description: LocalizedText? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("notification_id") val notificationId: String? = null,
    @SerializedName("entity_id") val entityId: String? = null
)
