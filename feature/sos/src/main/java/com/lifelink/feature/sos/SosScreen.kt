package com.lifelink.feature.sos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.lifelink.core.ui.components.LifeLinkUrgentButton
import com.lifelink.core.ui.components.VerticalSpace
import com.lifelink.feature.sos.service.SosForegroundService
import kotlinx.coroutines.delay

@Composable
fun SosScreen(onManageContacts: () -> Unit, viewModel: SosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    var hasPermission by remember { mutableStateOf(hasLocationPermission()) }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionsToRequest = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = granted
        if (granted) {
            viewModel.beginArming()
        } else {
            permissionDenied = true
        }
    }

    LaunchedEffect(uiState.sosState) {
        if (uiState.sosState == SosState.ARMING) {
            var remaining = 5
            while (remaining > 0) {
                delay(1000)
                remaining--
            }
            viewModel.confirmActivation()
            SosForegroundService.start(context)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (uiState.sosState) {
                SosState.IDLE -> {
                    if (!uiState.hasEmergencyContacts) {
                        Text(
                            "Add at least one emergency contact before enabling SOS.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                        VerticalSpace(16)
                        LifeLinkUrgentButton(text = "Add emergency contact", onClick = onManageContacts)
                    } else {
                        Text("Hold the button below for 5 seconds to trigger SOS.", style = MaterialTheme.typography.bodyLarge)
                        if (permissionDenied) {
                            VerticalSpace(12)
                            Text(
                                "Location permission is required for SOS to share your location. " +
                                        "Please allow it to continue, or enable it in system Settings.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        VerticalSpace(24)
                        LifeLinkUrgentButton(
                            text = "HOLD FOR SOS",
                            onClick = {
                                if (hasLocationPermission()) {
                                    viewModel.beginArming()
                                } else {
                                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                                }
                            },
                            modifier = Modifier.size(180.dp),
                            contentDescription = "Hold to trigger emergency SOS",
                        )
                    }
                }
                SosState.ARMING -> {
                    Text("Triggering SOS in a few seconds…", style = MaterialTheme.typography.titleLarge)
                    VerticalSpace(16)
                    Text("Tap Cancel now if this was accidental.", style = MaterialTheme.typography.bodyMedium)
                    VerticalSpace(24)
                    LifeLinkUrgentButton(text = "Cancel SOS", onClick = { viewModel.cancelArming() })
                }
                SosState.ACTIVE -> {
                    Text("SOS is ACTIVE", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                    VerticalSpace(12)
                    Text("Your location is being shared with your emergency contacts.", style = MaterialTheme.typography.bodyLarge)
                    VerticalSpace(24)
                    LifeLinkUrgentButton(
                        text = "I'm safe — stop SOS",
                        onClick = {
                            viewModel.deactivate()
                            SosForegroundService.stop(context)
                        },
                    )
                }
            }
        }
    }
}