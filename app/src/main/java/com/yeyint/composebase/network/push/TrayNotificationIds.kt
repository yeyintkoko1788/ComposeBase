package com.yeyint.composebase.network.push

/**
 * The Android tray id for a push about [entityId]. Derived from the thing the push is about, not
 * from the push's own notification id, so that every push about the same entity replaces the one
 * before it in the tray instead of piling up, and so that opening that entity's screen can find and
 * clear it:
 * `NotificationManagerCompat.from(context).cancel(trayNotificationIdFor(id))`.
 *
 * Not related to the backend's notification id, which is what the in-app notification list uses.
 */
fun trayNotificationIdFor(entityId: String): Int = entityId.hashCode()
