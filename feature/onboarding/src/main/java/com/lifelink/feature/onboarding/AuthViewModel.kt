package com.lifelink.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        _uiState.value = when (val result = authRepository.signIn(email, password)) {
            is LifeLinkResult.Success -> AuthUiState.Success
            is LifeLinkResult.Error -> AuthUiState.Error(result.message)
            LifeLinkResult.Loading -> AuthUiState.Loading
        }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        _uiState.value = when (val result = authRepository.register(email, password)) {
            is LifeLinkResult.Success -> AuthUiState.Success
            is LifeLinkResult.Error -> AuthUiState.Error(result.message)
            LifeLinkResult.Loading -> AuthUiState.Loading
        }
    }
}