package com.lifelink.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * M1 smoke test: proves Compose + Navigation + Hilt wiring is sound end-to-end by
 * launching MainActivity and asserting it doesn't crash. Feature-level Compose UI tests
 * live in each feature module as those screens are built out.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainActivity_launchesWithoutCrashing() {
        composeRule.waitForIdle()
    }
}
