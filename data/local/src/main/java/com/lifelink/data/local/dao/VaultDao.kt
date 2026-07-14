package com.lifelink.data.local.dao

import androidx.room.*
import com.lifelink.data.local.entity.VaultRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_records ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<VaultRecordEntity>>

    @Query("SELECT * FROM vault_records WHERE category = :category ORDER BY updatedAt DESC")
    fun observeByCategory(category: String): Flow<List<VaultRecordEntity>>

    @Upsert
    suspend fun upsert(record: VaultRecordEntity)

    @Delete
    suspend fun delete(record: VaultRecordEntity)

    @Query("DELETE FROM vault_records WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM vault_records WHERE isSynced = 0")
    suspend fun getUnsynced(): List<VaultRecordEntity>
}
