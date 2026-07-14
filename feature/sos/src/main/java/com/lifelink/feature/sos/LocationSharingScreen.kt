package com.lifelink.feature.sos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.LifeLinkPrimaryButton
import com.lifelink.core.ui.components.LifeLinkUrgentButton
import com.lifelink.core.ui.components.VerticalSpace
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun LocationSharingScreen(onBack: () -> Unit, viewModel: LocationSharingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Location") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            if (!uiState.isSharing) {
                Text("Share your live location with your emergency contacts for:", style = MaterialTheme.typography.bodyLarge)
                VerticalSpace(16)
                SharingDuration.entries.forEach { duration ->
                    DurationOption(duration, uiState.selectedDuration == duration) { viewModel.selectDuration(duration) }
                }
                VerticalSpace(24)
                // NOTE: real coordinates come from FusedLocationProviderClient in the SOS
                // module; kept null here to avoid duplicating a location-permission flow
                // across two screens in this milestone — see SosForegroundService for the
                // real fetch pattern this will call into.
                LifeLinkPrimaryButton(text = "Start sharing", onClick = { viewModel.startSharing(null, null) })
            } else {
                Text("Your location is being shared.", style = MaterialTheme.typography.titleLarge)
                VerticalSpace(24)
                LifeLinkUrgentButton(text = "Stop sharing", onClick = { viewModel.stopSharing() })
            }
        }
    }
}

@Composable
private fun DurationOption(duration: SharingDuration, selected: Boolean, onSelect: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(duration.label)
    }
}
