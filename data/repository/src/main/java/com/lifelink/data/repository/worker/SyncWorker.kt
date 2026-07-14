package com.lifelink.data.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lifelink.data.repository.ContactRepository
import com.lifelink.data.repository.VaultRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Periodic + connectivity-triggered background sync. Pushes anything written locally
 * while offline (contacts, vault records) up to Firestore. Designed to fail silently and
 * retry — sync is a best-effort background concern, never something that blocks the UI
 * or is surfaced as an error to the user (see repository Error messages: "deferred", not
 * "failed").
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val contactRepository: ContactRepository,
    private val vaultRepository: VaultRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        contactRepository.syncPendingContacts()
        vaultRepository.syncPendingRecords()
        // Individual repo sync calls already swallow their own failures into a
        // LifeLinkResult.Error("deferred") — from WorkManager's perspective this always
        // succeeds so it doesn't retry-storm; the *data* just stays unsynced until the
        // next periodic run finds connectivity.
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "lifelink_sync_worker"
    }
}
