package com.lifelink.data.repository

import kotlinx.coroutines.flow.Flow

data class EmergencyGuide(
    val id: String,
    val disasterType: String,
    val title: String,
    val steps: List<String>,
)

interface GuideRepository {
    fun observeGuides(disasterType: String? = null): Flow<List<EmergencyGuide>>
    /** Seeds bundled guides into Room on first launch. No-ops if already seeded. */
    suspend fun seedIfEmpty(guides: List<EmergencyGuide>)
}
