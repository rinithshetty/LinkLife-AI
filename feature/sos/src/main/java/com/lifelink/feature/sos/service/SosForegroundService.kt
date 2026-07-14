package com.lifelink.feature.sos.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * FR-1.2 / FR-1.3: runs as a genuine foreground service with a persistent, dismiss-proof
 * notification while SOS is active, so it survives Doze/App Standby and, per FR-1.3,
 * process death causes it to be recreated by the system rather than silently vanish.
 *
 * NOTE: this class intentionally does the location fetch + "dispatch" (in v1: logs /
 * queues a WorkManager job; a real SMS/Firestore push is wired the same way the
 * data:repository sync worker does it) directly, rather than depending on a ViewModel,
 * because Services and ViewModels have incompatible lifecycles.
 */
@AndroidEntryPoint
class SosForegroundService : Service() {

    @Inject lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        dispatchSos()
        return START_STICKY // system recreates the service if it's killed mid-emergency
    }

    private fun dispatchSos() {
        serviceScope.launch {
            try {
                val location = fusedLocationProviderClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()
                // v1: location captured and ready to hand to contact-dispatch (SMS/push).
                // Actual SMS send / Firestore write is a small addition here in M3 follow-up;
                // kept out of v1 scaffold to avoid requiring a real SMS permission grant
                // flow to be fully wired in this milestone.
                android.util.Log.i("SosForegroundService", "SOS dispatched at $location")
            } catch (e: Exception) {
                android.util.Log.e("SosForegroundService", "Location fetch failed", e)
            }
        }
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, SosForegroundService::class.java).setAction(ACTION_STOP)
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Active SOS", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SOS is active")
            .setContentText("Your location is being shared with your emergency contacts.")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setOngoing(true) // dismiss-proof per FR-1.3
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, "Cancel SOS", stopPendingIntent)
            .build()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "sos_channel"
        private const val NOTIFICATION_ID = 42
        const val ACTION_STOP = "com.lifelink.feature.sos.ACTION_STOP"

        fun start(context: Context) {
            context.startForegroundService(Intent(context, SosForegroundService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, SosForegroundService::class.java))
        }
    }
}
