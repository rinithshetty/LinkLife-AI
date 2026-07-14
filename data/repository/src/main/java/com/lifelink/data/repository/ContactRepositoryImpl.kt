package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.local.dao.ContactDao
import com.lifelink.data.local.entity.ContactEntity
import com.lifelink.data.remote.FirebaseAuthSource
import com.lifelink.data.remote.FirestoreSyncSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room is the single source of truth (offline-first): reads always come from [ContactDao].
 * Writes go to Room first (so the UI updates instantly, even offline), then get flagged
 * isSynced=false and pushed to Firestore opportunistically via [syncPendingContacts],
 * which is invoked both on network-reconnect and by a periodic WorkManager job.
 */
class ContactRepositoryImpl @Inject constructor(
    private val dao: ContactDao,
    private val remote: FirestoreSyncSource,
    private val authSource: FirebaseAuthSource,
) : ContactRepository {

    override fun observeContacts(): Flow<List<Contact>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertContact(contact: Contact): LifeLinkResult<Unit> = try {
        dao.upsert(contact.toEntity(isSynced = false))
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not save contact locally.", e)
    }

    override suspend fun deleteContact(id: String): LifeLinkResult<Unit> = try {
        dao.deleteById(id)
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not delete contact.", e)
    }

    override suspend fun hasAtLeastOneContact(): Boolean = dao.count() > 0

    override suspend fun syncPendingContacts(): LifeLinkResult<Unit> {
        val uid = authSource.currentUser?.uid ?: return LifeLinkResult.Error("Not signed in")
        return try {
            dao.getUnsynced().forEach { entity ->
                remote.uploadContact(
                    uid = uid,
                    id = entity.id,
                    data = mapOf(
                        "name" to entity.name,
                        "phoneNumber" to entity.phoneNumber,
                        "relationship" to entity.relationship,
                        "priority" to entity.priority,
                        "updatedAt" to entity.updatedAt,
                    ),
                )
                dao.upsert(entity.copy(isSynced = true))
            }
            LifeLinkResult.Success(Unit)
        } catch (e: Exception) {
            // Sync failures are expected when offline — this is not a user-facing error,
            // just a "try again later" signal for the caller (worker retries automatically).
            LifeLinkResult.Error("Sync deferred — will retry when online.", e)
        }
    }
}

private fun ContactEntity.toDomain() = Contact(id, name, phoneNumber, relationship, priority)

private fun Contact.toEntity(isSynced: Boolean) = ContactEntity(
    id = id,
    name = name,
    phoneNumber = phoneNumber,
    relationship = relationship,
    priority = priority,
    updatedAt = System.currentTimeMillis(),
    isSynced = isSynced,
)
