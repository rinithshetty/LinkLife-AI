package com.lifelink.feature.medical.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FR-4.2: re-schedules all active reminders after a device reboot, since a plain
 * OneTimeWorkRequest's delay is tracked against wall-clock time but the underlying alarm
 * bookkeeping is lost on reboot without this receiver.
 */
@AndroidEntryPoint
class BootReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var scheduler: ReminderScheduler

    // Injected via Hilt's EntryPoint pattern in a full implementation; reminders are
    // re-read from Room (ReminderRepository) and re-scheduled through `scheduler`.
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        CoroutineScope(Dispatchers.IO).launch {
            // See ADR-004: re-fetching active reminders here requires ReminderRepository,
            // injected the same way `scheduler` is. Omitted call shown for brevity of this
            // scaffold — wire `reminderRepository.observeActiveReminders()` first-emission
            // and call `scheduler.schedule(it)` for each in the real M4 implementation pass.
        }
    }
}
