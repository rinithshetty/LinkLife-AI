package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import kotlinx.coroutines.flow.Flow

data class MedicineReminder(
    val id: String,
    val medicineName: String,
    val dosage: String,
    val frequencyHours: Int,
    val nextTriggerAtMillis: Long,
    val isActive: Boolean,
)

interface ReminderRepository {
    fun observeActiveReminders(): Flow<List<MedicineReminder>>
    suspend fun upsertReminder(reminder: MedicineReminder): LifeLinkResult<Unit>
    suspend fun deactivateReminder(id: String): LifeLinkResult<Unit>
    suspend fun getDueReminders(nowMillis: Long): List<MedicineReminder>
}
