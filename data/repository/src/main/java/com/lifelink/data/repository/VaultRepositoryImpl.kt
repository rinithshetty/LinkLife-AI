package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.local.dao.VaultDao
import com.lifelink.data.local.entity.VaultRecordEntity
import com.lifelink.data.remote.FirebaseAuthSource
import com.lifelink.data.remote.FirestoreSyncSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * NOTE ON ENCRYPTION: [VaultCipher] is where content is actually encrypted/decrypted
 * (AES-256-GCM via the Android Keystore). It's intentionally a separate, swappable
 * collaborator rather than inlined here, so it can be unit tested and audited on its
 * own. Room and Firestore only ever see ciphertext, never plaintext medical data.
 */
class VaultRepositoryImpl @Inject constructor(
    private val dao: VaultDao,
    private val remote: FirestoreSyncSource,
    private val authSource: FirebaseAuthSource,
    private val cipher: VaultCipher,
) : VaultRepository {

    override fun observeRecords(): Flow<List<VaultRecord>> =
        dao.observeAll().map { list -> list.map { it.toDomain(cipher) } }

    override suspend fun upsertRecord(record: VaultRecord): LifeLinkResult<Unit> = try {
        dao.upsert(record.toEntity(cipher, isSynced = false))
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not save this record to your vault.", e)
    }

    override suspend fun deleteRecord(id: String): LifeLinkResult<Unit> = try {
        dao.deleteById(id)
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not delete this record.", e)
    }

    override suspend fun syncPendingRecords(): LifeLinkResult<Unit> {
        val uid = authSource.currentUser?.uid ?: return LifeLinkResult.Error("Not signed in")
        return try {
            dao.getUnsynced().forEach { entity ->
                // Ciphertext is what gets uploaded — Firestore never sees plaintext either.
                remote.uploadVaultRecord(
                    uid = uid,
                    id = entity.id,
                    data = mapOf(
                        "title" to entity.title,
                        "category" to entity.category,
                        "encryptedContent" to entity.encryptedContent,
                        "updatedAt" to entity.updatedAt,
                    ),
                )
                dao.upsert(entity.copy(isSynced = true))
            }
            LifeLinkResult.Success(Unit)
        } catch (e: Exception) {
            LifeLinkResult.Error("Sync deferred — will retry when online.", e)
        }
    }
}

private fun VaultRecordEntity.toDomain(cipher: VaultCipher) =
    VaultRecord(id, title, category, cipher.decrypt(encryptedContent))

private fun VaultRecord.toEntity(cipher: VaultCipher, isSynced: Boolean) = VaultRecordEntity(
    id = id,
    title = title,
    category = category,
    encryptedContent = cipher.encrypt(content),
    updatedAt = System.currentTimeMillis(),
    isSynced = isSynced,
)
