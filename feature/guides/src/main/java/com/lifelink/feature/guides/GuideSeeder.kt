package com.lifelink.feature.guides

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lifelink.data.repository.EmergencyGuide
import com.lifelink.data.repository.GuideRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Reads guides.json bundled in this module's assets and seeds Room on first launch.
 * This is the concrete mechanism behind "Offline Emergency Guides work in airplane mode
 * immediately after first install" (FR-6.1) — there is no network call anywhere in this
 * path, by construction.
 */
class GuideSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: GuideRepository,
) {
    private data class RawGuide(val id: String, val disasterType: String, val title: String, val steps: List<String>)

    suspend fun seedIfNeeded() {
        val json = context.assets.open("guides.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<RawGuide>>() {}.type
        val raw: List<RawGuide> = Gson().fromJson(json, type)
        repository.seedIfEmpty(raw.map { EmergencyGuide(it.id, it.disasterType, it.title, it.steps) })
    }
}
