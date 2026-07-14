package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult

data class Hospital(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double?,
)

interface HospitalRepository {
    suspend fun findNearbyHospitals(latitude: Double, longitude: Double): LifeLinkResult<List<Hospital>>
}