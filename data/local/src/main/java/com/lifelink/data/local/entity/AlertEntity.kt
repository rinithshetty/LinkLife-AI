package com.lifelink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disaster_alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val severity: String,
    val disasterType: String,
    val timestampMillis: Long,
    val isRead: Boolean = false,
)