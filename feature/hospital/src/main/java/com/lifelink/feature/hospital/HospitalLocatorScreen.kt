package com.lifelink.feature.hospital

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.lifelink.core.ui.components.EmptyState
import com.lifelink.core.ui.components.VerticalSpace
import com.lifelink.data.repository.Hospital

@Composable
fun HospitalLocatorScreen(onBack: () -> Unit, viewModel: HospitalLocatorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    var hasPermission by remember { mutableStateOf(hasLocationPermission()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        hasPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasPermission) viewModel.loadNearbyHospitals()
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            viewModel.loadNearbyHospitals()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Hospitals") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        when {
            !hasPermission -> EmptyState(
                "Location permission is needed to find hospitals near you.",
                modifier = Modifier.padding(padding),
            )
            uiState.isLoading -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            uiState.errorMessage != null -> EmptyState(uiState.errorMessage!!, modifier = Modifier.padding(padding))
            else -> Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                val userLat = uiState.userLatitude
                val userLng = uiState.userLongitude
                if (userLat != null && userLng != null) {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(LatLng(userLat, userLng), 13f)
                    }
                    GoogleMap(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        cameraPositionState = cameraPositionState,
                    ) {
                        Marker(state = MarkerState(position = LatLng(userLat, userLng)), title = "You")
                        uiState.hospitals.forEach { hospital ->
                            Marker(
                                state = MarkerState(position = LatLng(hospital.latitude, hospital.longitude)),
                                title = hospital.name,
                                snippet = hospital.address,
                            )
                        }
                    }
                }

                if (uiState.hospitals.isEmpty()) {
                    EmptyState("No hospitals found nearby.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        items(uiState.hospitals, key = { it.id }) { hospital ->
                            HospitalListItem(hospital)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HospitalListItem(hospital: Hospital) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(hospital.name, style = MaterialTheme.typography.titleMedium)
            VerticalSpace(4)
            Text(hospital.address, style = MaterialTheme.typography.bodyMedium)
            hospital.rating?.let {
                VerticalSpace(4)
                Text("Rating: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}