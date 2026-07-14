package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.local.dao.ReminderDao
import com.lifelink.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Purely local — reminders never need Firestore sync for v1; they're device-local alarms. */
class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao,
) : ReminderRepository {

    override fun observeActiveReminders(): Flow<List<MedicineReminder>> =
        dao.observeActive().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertReminder(reminder: MedicineReminder): LifeLinkResult<Unit> = try {
        dao.upsert(reminder.toEntity())
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not save reminder.", e)
    }

    override suspend fun deactivateReminder(id: String): LifeLinkResult<Unit> = try {
        dao.deactivateById(id)
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not update reminder.", e)
    }

    override suspend fun getDueReminders(nowMillis: Long): List<MedicineReminder> =
        dao.getDue(nowMillis).map { it.toDomain() }
}

private fun ReminderEntity.toDomain() = MedicineReminder(
    id, medicineName, dosage, frequencyHours, nextTriggerAtMillis, isActive,
)

private fun MedicineReminder.toEntity() = ReminderEntity(
    id = id,
    medicineName = medicineName,
    dosage = dosage,
    frequencyHours = frequencyHours,
    nextTriggerAtMillis = nextTriggerAtMillis,
    isActive = isActive,
    updatedAt = System.currentTimeMillis(),
)
