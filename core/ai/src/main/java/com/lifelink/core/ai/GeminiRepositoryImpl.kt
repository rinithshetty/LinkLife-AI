package com.lifelink.core.ai

import com.lifelink.core.common.DispatcherProvider
import com.lifelink.core.common.LifeLinkResult
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Real implementation backed by [GeminiApiService]. The API key is injected via
 * [GeminiConfig] rather than hardcoded — see core:ai/di/AiModule.kt for where it's wired
 * from local.properties / BuildConfig, so a real key is never committed to source control.
 */
class GeminiRepositoryImpl @Inject constructor(
    private val api: GeminiApiService,
    private val config: GeminiConfig,
    private val dispatchers: DispatcherProvider,
) : GeminiRepository {

    override suspend fun explainSymptoms(symptomDescription: String): LifeLinkResult<String> =
        callGemini(
            prompt = "A user describes these symptoms: \"$symptomDescription\". " +
                    "Explain, in plain non-alarming language, 2-3 *possible* general explanations. " +
                    "Do NOT diagnose. Do NOT recommend medication or dosages. Keep it under 150 words.",
        )

    override suspend fun summarizeMedicalRecord(rawText: String): LifeLinkResult<String> =
        callGemini(
            prompt = "Summarize the key facts (conditions, allergies, medications) from this " +
                    "medical note in plain bullet points, no interpretation added:\n\n$rawText",
        )

    override suspend fun generateEmergencyInstructions(
        disasterType: String,
        situation: String,
    ): LifeLinkResult<String> = callGemini(
        prompt = "Generate concise, numbered, step-by-step safety instructions for someone " +
                "currently experiencing a $disasterType, specifically: \"$situation\". " +
                "Prioritize immediate physical safety actions. Max 8 steps.",
    )

    override suspend fun analyzeOcrText(ocrText: String): LifeLinkResult<String> = callGemini(
        prompt = "The following text was OCR-scanned from a medicine label/prescription. " +
                "Identify the medicine name, dosage, and frequency if present. If the text is " +
                "unclear or incomplete, say so explicitly rather than guessing:\n\n$ocrText",
    )

    private suspend fun callGemini(prompt: String): LifeLinkResult<String> {
        val safeInput = MedicalSafetyWrapper.sanitizeInput(prompt)
            ?: return LifeLinkResult.Success(MedicalSafetyWrapper.safeRefusalMessage())

        if (config.apiKey.isBlank()) {
            android.util.Log.e("GeminiRepositoryImpl", "GEMINI_API_KEY is blank — set it in local.properties and rebuild.")
            return LifeLinkResult.Error("AI assistant isn't configured yet — no Gemini API key was found. Add GEMINI_API_KEY to local.properties and rebuild.")
        }

        return withContext(dispatchers.io) {
            try {
                val response = api.generateContent(
                    apiKey = config.apiKey,
                    request = GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(safeInput))))),
                )
                LifeLinkResult.Success(MedicalSafetyWrapper.wrapResponse(response.firstText()))
            } catch (e: Exception) {
                android.util.Log.e("GeminiRepositoryImpl", "Gemini call failed: ${e.javaClass.simpleName}: ${e.message}", e)
                LifeLinkResult.Error("AI assistant is unavailable right now. Check Logcat (tag: GeminiRepositoryImpl) for the exact reason.", e)
            }
        }
    }
}

/** Holds the Gemini API key, provided via Hilt from BuildConfig (see AiModule). */
data class GeminiConfig(val apiKey: String)