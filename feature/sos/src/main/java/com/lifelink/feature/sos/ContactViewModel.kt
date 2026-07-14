package com.lifelink.feature.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelink.data.repository.Contact
import com.lifelink.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ContactsUiState(val contacts: List<Contact> = emptyList())

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val repository: ContactRepository,
) : ViewModel() {

    val uiState: StateFlow<ContactsUiState> = repository.observeContacts()
        .map { ContactsUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactsUiState())

    fun addContact(name: String, phone: String, relationship: String) = viewModelScope.launch {
        repository.upsertContact(
            Contact(id = UUID.randomUUID().toString(), name = name, phoneNumber = phone, relationship = relationship, priority = 0),
        )
    }

    fun deleteContact(id: String) = viewModelScope.launch {
        repository.deleteContact(id)
    }
}
