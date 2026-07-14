package com.lifelink.core.ai

/**
 * Wraps every AI health-related response with a mandatory disclaimer and light
 * guardrails before it reaches the UI layer. Centralizing this in one object means the
 * disclaimer text changes in exactly one place if legal/compliance ever needs to update it.
 */
object MedicalSafetyWrapper {

    const val DISCLAIMER =
        "This is not a medical diagnosis. Please consult a qualified healthcare " +
            "professional for advice specific to your situation."

    private val REFUSAL_TRIGGERS = listOf(
        "dosage for overdose", "how to end my life", "lethal dose", "self harm method",
    )

    /**
     * Returns null (meaning: do not call the model / show a safe refusal instead) if the
     * raw user input matches an unsafe pattern. Otherwise returns the input unchanged.
     * This is a first, coarse client-side guard — NOT a substitute for server-side
     * moderation on whatever backend actually proxies the Gemini call in production.
     */
    fun sanitizeInput(userInput: String): String? {
        val normalized = userInput.lowercase()
        return if (REFUSAL_TRIGGERS.any { normalized.contains(it) }) null else userInput
    }

    fun wrapResponse(modelOutput: String): String = buildString {
        append(modelOutput.trim())
        append("\n\n")
        append(DISCLAIMER)
    }

    fun safeRefusalMessage(): String =
        "I can't help with that request. If you're in a medical emergency, please contact " +
            "local emergency services immediately, or use the SOS feature in this app."
}
