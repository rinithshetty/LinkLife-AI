package com.lifelink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single medical vault record (condition, allergy, or free-form note).
 * The `encryptedContent` field is expected to already be ciphertext by the time it
 * reaches this entity — encryption/decryption happens in the repository layer via
 * EncryptedFile/SQLCipher so Room never sees plaintext medical data. See ADR-002.
 */
@Entity(tableName = "vault_records")
data class VaultRecordEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "condition" | "allergy" | "prescription" | "note"
    val encryptedContent: String,
    val updatedAt: Long,
    val isSynced: Boolean = false,
)
