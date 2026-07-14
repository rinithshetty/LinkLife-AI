package com.lifelink.feature.hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.repository.Hospital
import com.lifelink.data.repository.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class HospitalLocatorUiState(
    val isLoading: Boolean = false,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val hospitals: List<Hospital> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class HospitalLocatorViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HospitalLocatorUiState())
    val uiState: StateFlow<HospitalLocatorUiState> = _uiState.asStateFlow()

    fun loadNearbyHospitals() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        try {
            val location = fusedLocationProviderClient
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .await()

            if (location == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Couldn't determine your location. Make sure location is enabled.",
                )
                return@launch
            }

            when (val result = hospitalRepository.findNearbyHospitals(location.latitude, location.longitude)) {
                is LifeLinkResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userLatitude = location.latitude,
                    userLongitude = location.longitude,
                    hospitals = result.data,
                )
                is LifeLinkResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                LifeLinkResult.Loading -> Unit
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Couldn't get your location: ${e.message}")
        }
    }
}