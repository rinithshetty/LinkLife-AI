package com.lifelink.data.local.dao

import androidx.room.*
import com.lifelink.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM disaster_alerts ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity)

    @Query("UPDATE disaster_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)
}