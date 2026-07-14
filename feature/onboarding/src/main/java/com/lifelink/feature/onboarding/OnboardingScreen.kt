package com.lifelink.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifelink.core.ui.components.LifeLinkPrimaryButton
import com.lifelink.core.ui.components.VerticalSpace

private data class OnboardingPage(val title: String, val body: String, val rationale: String)

/**
 * Explains WHY each permission will be requested before the OS prompt ever appears
 * (FR-9.2). This is a hard requirement, not a nicety — asking for ACCESS_FINE_LOCATION
 * cold, with no context, is the #1 reason users deny it and then SOS silently fails later.
 */
private val pages = listOf(
    OnboardingPage(
        title = "Welcome to LifeLink AI",
        body = "Your offline-first companion for medical emergencies and disasters.",
        rationale = "",
    ),
    OnboardingPage(
        title = "Location, only for emergencies",
        body = "We'll ask for location access so your SOS alerts and shared location reach the right place.",
        rationale = "Used only when you trigger SOS or start Location Sharing — never tracked in the background otherwise.",
    ),
    OnboardingPage(
        title = "Notifications for reminders",
        body = "We'll ask for notification access so medicine reminders and disaster alerts reach you on time.",
        rationale = "Used for medicine reminders and (optionally) disaster alerts. You can turn these off anytime in Settings.",
    ),
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var pageIndex by remember { mutableIntStateOf(0) }
    val page = pages[pageIndex]

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text(page.title, style = MaterialTheme.typography.headlineMedium)
            VerticalSpace(12)
            Text(page.body, style = MaterialTheme.typography.bodyLarge)
            if (page.rationale.isNotEmpty()) {
                VerticalSpace(8)
                Text(page.rationale, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
            VerticalSpace(32)
            LifeLinkPrimaryButton(
                text = if (pageIndex == pages.lastIndex) "Get started" else "Next",
                onClick = {
                    if (pageIndex == pages.lastIndex) onFinished() else pageIndex++
                },
            )
        }
    }
}
