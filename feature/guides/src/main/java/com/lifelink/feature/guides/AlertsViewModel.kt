package com.lifelink.feature.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.AlertRepository
import com.lifelink.data.repository.DisasterAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertsUiState(val alerts: List<DisasterAlert> = emptyList())

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: AlertRepository,
) : ViewModel() {

    val uiState: StateFlow<AlertsUiState> = repository.observeAlerts()
        .map { AlertsUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AlertsUiState())

    fun markRead(id: String) = viewModelScope.launch {
        repository.markRead(id)
    }
}