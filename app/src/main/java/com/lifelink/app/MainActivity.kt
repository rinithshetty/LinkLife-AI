package com.lifelink.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.lifelink.app.navigation.LifeLinkNavHost
import com.lifelink.core.ui.theme.LifeLinkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Accessibility prefs are read from DataStore inside AccessibilityViewModel in
            // a real build; kept as local state here so MainActivity has zero business
            // logic of its own (M1 principle: :app stays a thin composition root).
            var useLargeText by remember { mutableStateOf(false) }
            var forceDarkTheme by remember { mutableStateOf<Boolean?>(null) }

            LifeLinkTheme(
                darkTheme = forceDarkTheme ?: androidx.compose.foundation.isSystemInDarkTheme(),
                useLargeText = useLargeText,
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LifeLinkNavHost(
                        onToggleLargeText = { useLargeText = it },
                        onToggleDarkTheme = { forceDarkTheme = it },
                    )
                }
            }
        }
    }
}
