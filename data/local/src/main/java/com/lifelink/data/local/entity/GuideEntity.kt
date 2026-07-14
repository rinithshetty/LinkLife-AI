package com.lifelink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Seeded at first launch from bundled JSON assets (feature:guides/assets) — works fully offline. */
@Entity(tableName = "emergency_guides")
data class GuideEntity(
    @PrimaryKey val id: String,
    val disasterType: String, // "earthquake" | "flood" | "fire"
    val title: String,
    val stepsJson: String, // JSON-encoded List<String>, kept simple for v1
    val orderIndex: Int,
)
