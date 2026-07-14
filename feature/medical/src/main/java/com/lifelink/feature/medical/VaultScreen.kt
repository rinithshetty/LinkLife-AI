package com.lifelink.feature.medical

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

/** FR-5: encrypted-at-rest medical record vault. See VaultCipher in data:repository. */
@Composable
fun VaultScreen(onBack: () -> Unit, viewModel: VaultViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical Vault") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Filled.Add, contentDescription = "Add record") }
        },
    ) { padding ->
        if (uiState.records.isEmpty()) {
            EmptyState("Your vault is empty. Add conditions, allergies, or prescriptions.", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(uiState.records, key = { it.id }) { record ->
                    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(record.title, style = MaterialTheme.typography.titleMedium)
                                Text(record.category.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
                                Text(record.content, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = { viewModel.deleteRecord(record.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete ${record.title}")
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddVaultRecordDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, category, content ->
                    viewModel.addRecord(title, category, content)
                    showAddDialog = false
                },
            )
        }
    }
}

@Composable
private fun AddVaultRecordDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("condition") }
    var content by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add vault record", style = MaterialTheme.typography.titleLarge)
                VerticalSpace(16)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(8)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (condition/allergy/prescription/note)") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(8)
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Details") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(16)
                LifeLinkPrimaryButton(text = "Save", onClick = { onSave(title, category, content) })
            }
        }
    }
}
