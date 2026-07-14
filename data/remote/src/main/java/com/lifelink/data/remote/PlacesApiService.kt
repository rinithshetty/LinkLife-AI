package com.lifelink.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Places API (New) — the legacy GET-based nearbysearch endpoint this originally used was
 * fully deprecated by Google and now returns REQUEST_DENIED for new Cloud projects. This
 * is the current POST-based replacement (different request/response shape entirely).
 */
interface PlacesApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/places:searchNearby")
    suspend fun searchNearby(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: SearchNearbyRequest,
    ): SearchNearbyResponse
}

data class SearchNearbyRequest(
    val includedTypes: List<String>,
    val maxResultCount: Int,
    val locationRestriction: LocationRestriction,
)

data class LocationRestriction(val circle: Circle)
data class Circle(val center: LatLngNew, val radius: Double)
data class LatLngNew(val latitude: Double, val longitude: Double)

data class SearchNearbyResponse(val places: List<PlaceResult> = emptyList())

data class PlaceResult(
    val id: String = "",
    val displayName: DisplayName? = null,
    val formattedAddress: String = "",
    val location: LatLngNew? = null,
    val rating: Double? = null,
)

data class DisplayName(val text: String = "", val languageCode: String = "")