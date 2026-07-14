package com.lifelink.data.local.dao

import androidx.room.*
import com.lifelink.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM medicine_reminders WHERE isActive = 1 ORDER BY nextTriggerAtMillis ASC")
    fun observeActive(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM medicine_reminders WHERE nextTriggerAtMillis <= :nowMillis AND isActive = 1")
    suspend fun getDue(nowMillis: Long): List<ReminderEntity>

    @Upsert
    suspend fun upsert(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("UPDATE medicine_reminders SET isActive = 0 WHERE id = :id")
    suspend fun deactivateById(id: String)
}
