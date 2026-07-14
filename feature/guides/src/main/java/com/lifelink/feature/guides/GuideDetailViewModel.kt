package com.lifelink.feature.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.EmergencyGuide
import com.lifelink.data.repository.GuideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuideDetailViewModel @Inject constructor(
    private val repository: GuideRepository,
) : ViewModel() {

    private val _guide = MutableStateFlow<EmergencyGuide?>(null)
    val guide: StateFlow<EmergencyGuide?> = _guide.asStateFlow()

    fun load(guideId: String) = viewModelScope.launch {
        repository.observeGuides().collect { list ->
            _guide.value = list.firstOrNull { it.id == guideId }
        }
    }
}
