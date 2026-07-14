package com.lifelink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val email: String,
    val nickname: String,
    val addedAtMillis: Long,
)