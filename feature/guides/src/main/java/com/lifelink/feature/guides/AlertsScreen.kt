package com.lifelink.feature.guides

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.EmptyState
import com.lifelink.data.repository.DisasterAlert
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertsScreen(onBack: () -> Unit, viewModel: AlertsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = androidx.compose.runtime.remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disaster Alerts") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        if (uiState.alerts.isEmpty()) {
            EmptyState("No alerts yet. You'll see disaster alerts here as they arrive.", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(uiState.alerts, key = { it.id }) { alert ->
                    AlertCard(alert, dateFormat, onClick = { viewModel.markRead(alert.id) })
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: DisasterAlert, dateFormat: SimpleDateFormat, onClick: () -> Unit) {
    val severityColor = when (alert.severity.uppercase()) {
        "CRITICAL", "HIGH" -> MaterialTheme.colorScheme.error
        "MEDIUM" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        onClick = onClick,
        colors = if (!alert.isRead) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        } else {
            CardDefaults.cardColors()
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(alert.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(alert.severity.uppercase(), style = MaterialTheme.typography.labelLarge, color = severityColor)
            }
            Text(alert.message, style = MaterialTheme.typography.bodyMedium)
            Text(dateFormat.format(Date(alert.timestampMillis)), style = MaterialTheme.typography.bodySmall)
        }
    }
}