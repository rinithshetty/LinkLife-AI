package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import kotlinx.coroutines.flow.Flow

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val priority: Int,
)

interface ContactRepository {
    fun observeContacts(): Flow<List<Contact>>
    suspend fun upsertContact(contact: Contact): LifeLinkResult<Unit>
    suspend fun deleteContact(id: String): LifeLinkResult<Unit>
    suspend fun hasAtLeastOneContact(): Boolean
    suspend fun syncPendingContacts(): LifeLinkResult<Unit>
}
