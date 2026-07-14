package com.lifelink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val priority: Int,
    val updatedAt: Long,
    val isSynced: Boolean = false,
)
