package com.lifelink.feature.medical.worker

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.lifelink.data.repository.MedicineReminder
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/** Thin wrapper so ViewModels never touch WorkManager APIs directly (keeps them testable). */
class ReminderScheduler @Inject constructor(
    private val workManager: WorkManager,
) {
    fun schedule(reminder: MedicineReminder) {
        val delayMillis = (reminder.nextTriggerAtMillis - System.currentTimeMillis()).coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder()
                    .putString(ReminderNotificationWorker.KEY_REMINDER_ID, reminder.id)
                    .putString(ReminderNotificationWorker.KEY_MEDICINE_NAME, reminder.medicineName)
                    .putString(ReminderNotificationWorker.KEY_DOSAGE, reminder.dosage)
                    .build(),
            )
            .addTag(tagFor(reminder.id))
            .build()

        workManager.enqueueUniqueWork(tagFor(reminder.id), ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(reminderId: String) {
        workManager.cancelUniqueWork(tagFor(reminderId))
    }

    private fun tagFor(reminderId: String) = "reminder_$reminderId"
}
