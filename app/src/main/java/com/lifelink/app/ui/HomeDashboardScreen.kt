package com.lifelink.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lifelink.app.navigation.Destination

private data class DashboardTile(val label: String, val icon: ImageVector, val destination: Destination, val urgent: Boolean = false)

/**
 * Landing screen after auth. Deliberately a simple tile grid, not a bottom-nav-per-feature
 * layout — with 8 Tier-1/2 features, a grid keeps everything one tap away without forcing
 * an arbitrary "which 5 get bottom nav slots" decision.
 */
@Composable
fun HomeDashboardScreen(onNavigate: (Destination) -> Unit) {
    val tiles = listOf(
        DashboardTile("Emergency SOS", Icons.Filled.Warning, Destination.Sos, urgent = true),
        DashboardTile("Emergency Contacts", Icons.Filled.Call, Destination.Contacts),
        DashboardTile("Share Location", Icons.Filled.LocationOn, Destination.LocationSharing),
        DashboardTile("Medical Vault", Icons.Filled.Lock, Destination.Vault),
        DashboardTile("Medicine Reminders", Icons.Filled.Notifications, Destination.Reminders),
        DashboardTile("Emergency Guides", Icons.Filled.Menu, Destination.Guides),
        DashboardTile("AI Symptom Explainer", Icons.Filled.Info, Destination.Assistant),
        DashboardTile("Nearby Hospitals", Icons.Filled.Place, Destination.HospitalLocator),
        DashboardTile("Medicine Scanner", Icons.Filled.CameraAlt, Destination.OcrScanner),
        DashboardTile("Disaster Alerts", Icons.Filled.NotificationsActive, Destination.Alerts),
        DashboardTile("Family Check-in", Icons.Filled.People, Destination.FamilyCheckIn),
        DashboardTile("Settings", Icons.Filled.Settings, Destination.Settings),
    )

    Scaffold(topBar = { TopAppBar(title = { Text("LifeLink AI") }) }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, padding.calculateTopPadding() + 8.dp, 16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(tiles) { tile ->
                Card(
                    onClick = { onNavigate(tile.destination) },
                    colors = if (tile.urgent) {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    } else {
                        CardDefaults.cardColors()
                    },
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(tile.icon, contentDescription = null)
                        Text(tile.label, style = MaterialTheme.typography.titleMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
    }
}
