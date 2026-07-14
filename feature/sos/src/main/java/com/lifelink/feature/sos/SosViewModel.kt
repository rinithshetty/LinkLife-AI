package com.lifelink.feature.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SosState { IDLE, ARMING, ACTIVE }

data class SosUiState(
    val sosState: SosState = SosState.IDLE,
    val hasEmergencyContacts: Boolean = true,
    val armingSecondsRemaining: Int = 5,
)

/**
 * Owns SOS trigger/cancel intent only — the actual foreground service lifecycle
 * (SosForegroundService) is started/stopped by the screen via context, since a Service
 * needs an Android Context the ViewModel shouldn't hold a reference to (leak risk).
 */
@HiltViewModel
class SosViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : ViewModel() {

    private val _sosState = MutableStateFlow(SosState.IDLE)

    val uiState: StateFlow<SosUiState> = _sosState.map { state ->
        SosUiState(sosState = state, hasEmergencyContacts = contactRepository.hasAtLeastOneContact())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SosUiState())

    fun beginArming() {
        _sosState.value = SosState.ARMING
    }

    fun cancelArming() {
        _sosState.value = SosState.IDLE
    }

    fun confirmActivation() {
        _sosState.value = SosState.ACTIVE
    }

    fun deactivate() {
        _sosState.value = SosState.IDLE
    }

    fun checkContactsGate(onResult: (Boolean) -> Unit) = viewModelScope.launch {
        onResult(contactRepository.hasAtLeastOneContact())
    }
}
