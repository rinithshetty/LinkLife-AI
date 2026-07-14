package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import kotlinx.coroutines.flow.Flow

data class VaultRecord(
    val id: String,
    val title: String,
    val category: String,
    val content: String, // plaintext at the domain boundary; encrypted at rest by the impl
)

interface VaultRepository {
    fun observeRecords(): Flow<List<VaultRecord>>
    suspend fun upsertRecord(record: VaultRecord): LifeLinkResult<Unit>
    suspend fun deleteRecord(id: String): LifeLinkResult<Unit>
    suspend fun syncPendingRecords(): LifeLinkResult<Unit>
}
