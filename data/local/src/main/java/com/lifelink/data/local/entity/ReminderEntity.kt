package com.lifelink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val medicineName: String,
    val dosage: String,
    val frequencyHours: Int,
    val nextTriggerAtMillis: Long,
    val isActive: Boolean = true,
    val updatedAt: Long,
)
