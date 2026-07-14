package com.lifelink.feature.guides

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifelink.data.repository.AlertRepository
import com.lifelink.data.repository.DisasterAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class LifeLinkMessagingService : FirebaseMessagingService() {

    @Inject lateinit var alertRepository: AlertRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Disaster Alert"
        val body = message.notification?.body ?: message.data["message"] ?: "Check the Alerts screen for details."
        val severity = message.data["severity"] ?: "MEDIUM"
        val disasterType = message.data["disasterType"] ?: "general"

        val alert = DisasterAlert(
            id = UUID.randomUUID().toString(),
            title = title,
            message = body,
            severity = severity,
            disasterType = disasterType,
            timestampMillis = System.currentTimeMillis(),
            isRead = false,
        )

        serviceScope.launch { alertRepository.saveAlert(alert) }
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Disaster Alerts", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        android.util.Log.i("LifeLinkMessagingService", "New FCM token: $token")
    }

    companion object {
        private const val CHANNEL_ID = "disaster_alerts_channel"
    }
}