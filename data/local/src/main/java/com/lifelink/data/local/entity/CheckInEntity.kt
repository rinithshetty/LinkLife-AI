package com.lifelink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "safety_checkins")
data class CheckInEntity(
    @PrimaryKey val id: String,
    val status: String, // "SAFE" | "NEEDS_HELP" | "UNKNOWN"
    val latitude: Double?,
    val longitude: Double?,
    val timestampMillis: Long,
    val isSynced: Boolean = false,
)
