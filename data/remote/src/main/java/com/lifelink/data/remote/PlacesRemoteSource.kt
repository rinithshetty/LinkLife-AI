package com.lifelink.data.remote

import javax.inject.Inject

class PlacesRemoteSource @Inject constructor(
    private val api: PlacesApiService,
    private val config: PlacesConfig,
) {
    suspend fun findNearbyHospitals(latitude: Double, longitude: Double, radiusMeters: Double = 5000.0): List<PlaceResult> =
        api.searchNearby(
            apiKey = config.apiKey,
            fieldMask = "places.id,places.displayName,places.formattedAddress,places.location,places.rating",
            request = SearchNearbyRequest(
                includedTypes = listOf("hospital"),
                maxResultCount = 20,
                locationRestriction = LocationRestriction(
                    circle = Circle(center = LatLngNew(latitude, longitude), radius = radiusMeters),
                ),
            ),
        ).places
}

data class PlacesConfig(val apiKey: String)