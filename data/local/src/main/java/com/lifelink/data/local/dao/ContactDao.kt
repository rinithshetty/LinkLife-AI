package com.lifelink.data.local.dao

import androidx.room.*
import com.lifelink.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY priority ASC")
    fun observeAll(): Flow<List<ContactEntity>>

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun count(): Int

    @Upsert
    suspend fun upsert(contact: ContactEntity)

    @Delete
    suspend fun delete(contact: ContactEntity)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM emergency_contacts WHERE isSynced = 0")
    suspend fun getUnsynced(): List<ContactEntity>
}
