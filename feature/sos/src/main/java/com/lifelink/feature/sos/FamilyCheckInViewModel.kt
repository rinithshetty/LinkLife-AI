package com.lifelink.feature.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.repository.CheckInRepository
import com.lifelink.data.repository.FamilyMember
import com.lifelink.data.repository.FamilyMemberStatus
import com.lifelink.data.repository.FamilyRepository
import com.lifelink.data.repository.SafetyStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FamilyCheckInUiState(
    val members: List<FamilyMember> = emptyList(),
    val memberStatuses: Map<String, FamilyMemberStatus?> = emptyMap(),
    val isPublishing: Boolean = false,
    val lastPublishError: String? = null,
)

@HiltViewModel
class FamilyCheckInViewModel @Inject constructor(
    private val familyRepository: FamilyRepository,
    private val checkInRepository: CheckInRepository,
) : ViewModel() {

    private val _memberStatuses = MutableStateFlow<Map<String, FamilyMemberStatus?>>(emptyMap())
    private val _isPublishing = MutableStateFlow(false)
    private val _lastPublishError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FamilyCheckInUiState> = combine(
        familyRepository.observeFamilyMembers(),
        _memberStatuses,
        _isPublishing,
        _lastPublishError,
    ) { members, statuses, publishing, error ->
        FamilyCheckInUiState(members, statuses, publishing, error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FamilyCheckInUiState())

    fun addMember(email: String, nickname: String) = viewModelScope.launch {
        familyRepository.addFamilyMember(email, nickname)
    }

    fun removeMember(id: String) = viewModelScope.launch {
        familyRepository.removeFamilyMember(id)
    }

    fun refreshStatus(email: String) = viewModelScope.launch {
        when (val result = familyRepository.fetchMemberStatus(email)) {
            is LifeLinkResult.Success -> _memberStatuses.value = _memberStatuses.value + (email to result.data)
            is LifeLinkResult.Error -> _lastPublishError.value = result.message
            LifeLinkResult.Loading -> Unit
        }
    }

    fun markMyselfSafe() = viewModelScope.launch {
        _isPublishing.value = true
        _lastPublishError.value = null
        checkInRepository.recordCheckIn(SafetyStatus.SAFE, latitude = null, longitude = null)
        when (val result = checkInRepository.publishStatusPublicly(SafetyStatus.SAFE)) {
            is LifeLinkResult.Error -> _lastPublishError.value = result.message
            else -> Unit
        }
        _isPublishing.value = false
    }
}