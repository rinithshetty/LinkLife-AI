package com.lifelink.feature.sos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.EmptyState
import com.lifelink.core.ui.components.LifeLinkPrimaryButton
import com.lifelink.core.ui.components.VerticalSpace

@Composable
fun ContactsScreen(onBack: () -> Unit, viewModel: ContactViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Contacts") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add contact")
            }
        },
    ) { padding ->
        if (uiState.contacts.isEmpty()) {
            EmptyState("No emergency contacts yet. Add at least one to enable SOS.", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(uiState.contacts, key = { it.id }) { contact ->
                    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(contact.name, style = MaterialTheme.typography.titleMedium)
                                Text(contact.relationship, style = MaterialTheme.typography.bodyMedium)
                                Text(contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = { viewModel.deleteContact(contact.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete ${contact.name}")
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddContactDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, phone, relationship ->
                    viewModel.addContact(name, phone, relationship)
                    showAddDialog = false
                },
            )
        }
    }
}

@Composable
private fun AddContactDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add emergency contact", style = MaterialTheme.typography.titleLarge)
                VerticalSpace(16)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(8)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone number") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(8)
                OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text("Relationship") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(16)
                LifeLinkPrimaryButton(text = "Save", onClick = { onSave(name, phone, relationship) })
            }
        }
    }
}
