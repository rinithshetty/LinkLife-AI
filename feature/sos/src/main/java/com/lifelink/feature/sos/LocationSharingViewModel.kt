package com.lifelink.feature.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.CheckInRepository
import com.lifelink.data.repository.SafetyStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SharingDuration(val minutes: Int, val label: String) {
    FIFTEEN_MIN(15, "15 minutes"),
    ONE_HOUR(60, "1 hour"),
    UNTIL_STOPPED(-1, "Until I stop it"),
}

data class LocationSharingUiState(
    val isSharing: Boolean = false,
    val selectedDuration: SharingDuration = SharingDuration.FIFTEEN_MIN,
)

@HiltViewModel
class LocationSharingViewModel @Inject constructor(
    private val checkInRepository: CheckInRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationSharingUiState())
    val uiState: StateFlow<LocationSharingUiState> = _uiState.asStateFlow()

    fun selectDuration(duration: SharingDuration) {
        _uiState.value = _uiState.value.copy(selectedDuration = duration)
    }

    fun startSharing(latitude: Double?, longitude: Double?) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isSharing = true)
        checkInRepository.recordCheckIn(SafetyStatus.SAFE, latitude, longitude)
    }

    fun stopSharing() {
        _uiState.value = _uiState.value.copy(isSharing = false)
    }
}
