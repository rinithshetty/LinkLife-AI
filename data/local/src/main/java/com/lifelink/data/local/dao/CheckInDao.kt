package com.lifelink.data.local.dao

import androidx.room.*
import com.lifelink.data.local.entity.CheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM safety_checkins ORDER BY timestampMillis DESC LIMIT 1")
    fun observeLatest(): Flow<CheckInEntity?>

    @Insert
    suspend fun insert(checkIn: CheckInEntity)

    @Query("SELECT * FROM safety_checkins WHERE isSynced = 0")
    suspend fun getUnsynced(): List<CheckInEntity>
}
