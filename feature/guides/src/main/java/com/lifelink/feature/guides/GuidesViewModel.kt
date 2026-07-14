package com.lifelink.feature.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.EmergencyGuide
import com.lifelink.data.repository.GuideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuidesUiState(val guides: List<EmergencyGuide> = emptyList())

@HiltViewModel
class GuidesViewModel @Inject constructor(
    private val repository: GuideRepository,
    private val seeder: GuideSeeder,
) : ViewModel() {

    val uiState: StateFlow<GuidesUiState> = repository.observeGuides()
        .map { GuidesUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GuidesUiState())

    init {
        viewModelScope.launch { seeder.seedIfNeeded() }
    }
}
