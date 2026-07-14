package com.lifelink.feature.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.core.ai.GeminiRepository
import com.lifelink.core.common.LifeLinkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(val text: String, val isUser: Boolean)

data class AssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
)

/**
 * FR-7: AI Symptom Explainer. Every response returned by [GeminiRepository] already
 * carries the mandatory disclaimer (see MedicalSafetyWrapper) — this ViewModel never
 * needs to add it again, which is what guarantees FR-7.2 (100% of responses disclaimed)
 * regardless of which screen calls into core:ai.
 */
@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        val current = _uiState.value
        _uiState.value = current.copy(messages = current.messages + ChatMessage(text, isUser = true), isLoading = true)

        viewModelScope.launch {
            when (val result = geminiRepository.explainSymptoms(text)) {
                is LifeLinkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + ChatMessage(result.data, isUser = false),
                        isLoading = false,
                    )
                }
                is LifeLinkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + ChatMessage(result.message, isUser = false),
                        isLoading = false,
                        isOffline = true,
                    )
                }
                LifeLinkResult.Loading -> Unit
            }
        }
    }
}
