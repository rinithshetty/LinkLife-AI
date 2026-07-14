package com.lifelink.feature.sos

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
import androidx.compose.material.icons.filled.Refresh
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
import com.lifelink.data.repository.FamilyMember

@Composable
fun FamilyCheckInScreen(onBack: () -> Unit, viewModel: FamilyCheckInViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Safety Check-in") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Filled.Add, contentDescription = "Add family member") }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LifeLinkPrimaryButton(
                text = "I'm safe — let family know",
                loading = uiState.isPublishing,
                onClick = { viewModel.markMyselfSafe() },
                modifier = Modifier.padding(16.dp),
            )

            uiState.lastPublishError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
                VerticalSpace(8)
            }

            if (uiState.members.isEmpty()) {
                EmptyState("No family members added yet. Add someone by email to check their status.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.members, key = { it.id }) { member ->
                        FamilyMemberRow(
                            member = member,
                            status = uiState.memberStatuses[member.email],
                            onRefresh = { viewModel.refreshStatus(member.email) },
                            onRemove = { viewModel.removeMember(member.id) },
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddFamilyMemberDialog(
                onDismiss = { showAddDialog = false },
                onSave = { email, nickname ->
                    viewModel.addMember(email, nickname)
                    showAddDialog = false
                },
            )
        }
    }
}

@Composable
private fun FamilyMemberRow(
    member: FamilyMember,
    status: com.lifelink.data.repository.FamilyMemberStatus?,
    onRefresh: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(member.nickname, style = MaterialTheme.typography.titleMedium)
                Text(member.email, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = status?.let { "Status: ${it.status}" } ?: "Status: unknown — tap refresh",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (status?.status == "SAFE") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                )
            }
            IconButton(onClick = onRefresh) { Icon(Icons.Filled.Refresh, contentDescription = "Check ${member.nickname}'s status") }
            IconButton(onClick = onRemove) { Icon(Icons.Filled.Delete, contentDescription = "Remove ${member.nickname}") }
        }
    }
}

@Composable
private fun AddFamilyMemberDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add family member", style = MaterialTheme.typography.titleLarge)
                VerticalSpace(16)
                OutlinedTextField(value = nickname, onValueChange = { nickname = it }, label = { Text("Nickname") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(8)
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Their email (same one they sign in with)") }, modifier = Modifier.fillMaxWidth())
                VerticalSpace(16)
                LifeLinkPrimaryButton(text = "Add", onClick = { onSave(email, nickname) })
            }
        }
    }
}