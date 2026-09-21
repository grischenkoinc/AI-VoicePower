package com.aivoicepower.service

import com.aivoicepower.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class VoicePowerMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: return
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: return

        NotificationHelper.createNotificationChannel(applicationContext)
        NotificationHelper.showCoachNotification(applicationContext, title, body)
    }

    override fun onNewToken(token: String) {
        // Token оновлюється автоматично — нічого додаткового не потрібно
        // для адресних розсилок можна зберігати в Firestore
    }
}
