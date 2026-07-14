package com.lifelink.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Accessibility Mode lives here: large text + dark mode override, per FR-8 in the PRD. */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onToggleLargeText: (Boolean) -> Unit,
    onToggleDarkTheme: (Boolean?) -> Unit,
) {
    var largeText by remember { mutableStateOf(false) }
    var darkModeOverride by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Accessibility & Settings") }) }) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Large text", modifier = Modifier.weight(1f))
                Switch(
                    checked = largeText,
                    onCheckedChange = {
                        largeText = it
                        onToggleLargeText(it)
                    },
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("Force dark mode", modifier = Modifier.weight(1f))
                Switch(
                    checked = darkModeOverride,
                    onCheckedChange = {
                        darkModeOverride = it
                        onToggleDarkTheme(if (it) true else null)
                    },
                )
            }
        }
    }
}
