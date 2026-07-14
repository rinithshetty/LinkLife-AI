package com.lifelink.core.ai

import com.lifelink.core.common.LifeLinkResult

/**
 * Abstraction over the Gemini API. Feature modules depend on this interface only —
 * never on Retrofit/OkHttp types directly — so AI calls are mockable in ViewModel tests
 * and the underlying model/provider can be swapped without touching feature code.
 *
 * IMPORTANT SAFETY CONTRACT:
 * Every method here returns text that MUST be passed through [MedicalSafetyWrapper]
 * before being shown to the user. Implementations do not do this themselves — it is
 * enforced at the call site so it's impossible to silently forget in a new feature.
 */
interface GeminiRepository {
    suspend fun explainSymptoms(symptomDescription: String): LifeLinkResult<String>
    suspend fun summarizeMedicalRecord(rawText: String): LifeLinkResult<String>
    suspend fun generateEmergencyInstructions(disasterType: String, situation: String): LifeLinkResult<String>
    suspend fun analyzeOcrText(ocrText: String): LifeLinkResult<String>
}
