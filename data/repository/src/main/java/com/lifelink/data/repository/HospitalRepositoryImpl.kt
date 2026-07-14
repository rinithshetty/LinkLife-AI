package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.remote.PlaceResult
import com.lifelink.data.remote.PlacesRemoteSource
import javax.inject.Inject

class HospitalRepositoryImpl @Inject constructor(
    private val remoteSource: PlacesRemoteSource,
) : HospitalRepository {

    override suspend fun findNearbyHospitals(latitude: Double, longitude: Double): LifeLinkResult<List<Hospital>> = try {
        val hospitals = remoteSource.findNearbyHospitals(latitude, longitude).mapNotNull { it.toDomain() }
        LifeLinkResult.Success(hospitals)
    } catch (e: Exception) {
        LifeLinkResult.Error("Couldn't load nearby hospitals. Check your connection and try again.", e)
    }
}

private fun PlaceResult.toDomain(): Hospital? {
    val loc = location ?: return null
    return Hospital(
        id = id,
        name = displayName?.text ?: "Unknown hospital",
        address = formattedAddress,
        latitude = loc.latitude,
        longitude = loc.longitude,
        rating = rating,
    )
}