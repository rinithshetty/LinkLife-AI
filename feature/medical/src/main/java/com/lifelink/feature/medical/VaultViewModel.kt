package com.lifelink.feature.medical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.VaultRecord
import com.lifelink.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class VaultUiState(val records: List<VaultRecord> = emptyList())

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val repository: VaultRepository,
) : ViewModel() {

    val uiState: StateFlow<VaultUiState> = repository.observeRecords()
        .map { VaultUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VaultUiState())

    fun addRecord(title: String, category: String, content: String) = viewModelScope.launch {
        repository.upsertRecord(VaultRecord(id = UUID.randomUUID().toString(), title = title, category = category, content = content))
    }

    fun deleteRecord(id: String) = viewModelScope.launch {
        repository.deleteRecord(id)
    }
}
