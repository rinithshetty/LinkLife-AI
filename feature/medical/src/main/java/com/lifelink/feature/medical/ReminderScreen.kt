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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.EmptyState
import com.lifelink.core.ui.components.LifeLinkPrimaryButton
import com.lifelink.core.ui.components.VerticalSpace
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderScreen(onBack: () -> Unit, viewModel: ReminderViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Reminders") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Filled.Add, contentDescription = "Add reminder") }
        },
    ) { padding ->
        if (uiState.reminders.isEmpty()) {
            EmptyState("No reminders set. Add your first medicine reminder.", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(uiState.reminders, key = { it.id }) { reminder ->
                    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(reminder.medicineName, style = MaterialTheme.typography.titleMedium)
                                Text(reminder.dosage, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Next: ${timeFormat.format(Date(reminder.nextTriggerAtMillis))} · every ${reminder.frequencyHours}h",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            IconButton(onClick = { viewModel.removeReminder(reminder.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove ${reminder.medicineName}")
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, dosage, hours ->
                    viewModel.addReminder(name, dosage, hours)
                    showAddDialog = false
                },
            )
        }
    }
}

@Composable
private fun AddReminderDialog(onDismiss: () -> Unit, onSave: (String, String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var hoursText by remember { mutableStateOf("8") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add medicine reminder", style = MaterialTheme.typography.titleLarge)
                VerticalSpace(16)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Medicine name") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(8)
                OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage (e.g. 500mg)") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(8)
                OutlinedTextField(
                    value = hoursText,
                    onValueChange = { hoursText = it },
                    label = { Text("Repeat every (hours)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                VerticalSpace(16)
                LifeLinkPrimaryButton(
                    text = "Save",
                    onClick = { onSave(name, dosage, hoursText.toIntOrNull() ?: 8) },
                )
            }
        }
    }
}
