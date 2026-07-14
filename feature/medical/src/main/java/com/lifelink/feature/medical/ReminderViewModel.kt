package com.lifelink.feature.medical

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.MedicineReminder
import com.lifelink.data.repository.ReminderRepository
import com.lifelink.feature.medical.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ReminderUiState(val reminders: List<MedicineReminder> = emptyList())

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler,
) : ViewModel() {

    val uiState: StateFlow<ReminderUiState> = repository.observeActiveReminders()
        .map { ReminderUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReminderUiState())

    fun addReminder(medicineName: String, dosage: String, frequencyHours: Int) = viewModelScope.launch {
        val reminder = MedicineReminder(
            id = UUID.randomUUID().toString(),
            medicineName = medicineName,
            dosage = dosage,
            frequencyHours = frequencyHours,
            nextTriggerAtMillis = System.currentTimeMillis() + frequencyHours * 3_600_000L,
            isActive = true,
        )
        repository.upsertReminder(reminder)
        scheduler.schedule(reminder)
    }

    fun removeReminder(id: String) = viewModelScope.launch {
        repository.deactivateReminder(id)
        scheduler.cancel(id)
    }
}
