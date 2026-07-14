package com.lifelink.feature.medical.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Fires a single medicine-reminder notification. Scheduled per-reminder by
 * [ReminderScheduler] with a WorkManager one-time request whose initial delay is the time
 * until `nextTriggerAtMillis` — this fires reliably regardless of connectivity (FR-4.2)
 * and, combined with [BootReminderReceiver], survives device reboot.
 */
@HiltWorker
class ReminderNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val medicineName = inputData.getString(KEY_MEDICINE_NAME) ?: "your medicine"
        val dosage = inputData.getString(KEY_DOSAGE) ?: ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Medicine Reminders", NotificationManager.IMPORTANCE_HIGH)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val canPostNotifications = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        if (canPostNotifications) {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Time to take $medicineName")
                .setContentText(dosage)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setAutoCancel(true)
                .build()
            androidx.core.app.NotificationManagerCompat.from(context).notify(inputData.getString(KEY_REMINDER_ID).hashCode(), notification)
        }

        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "medicine_reminder_channel"
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_MEDICINE_NAME = "medicine_name"
        const val KEY_DOSAGE = "dosage"
    }
}
