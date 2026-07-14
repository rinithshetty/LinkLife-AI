package com.lifelink.core.ai

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Raw Retrofit contract for the Gemini generateContent endpoint. Not marked `internal`
 * because GeminiRepositoryImpl (public, for Hilt's @Binds) takes it as a constructor
 * parameter — Kotlin doesn't allow a public API to expose an internal type in its
 * signature.
 */
interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest,
    ): GeminiResponse
}

data class GeminiRequest(val contents: List<GeminiContent>)
data class GeminiContent(val parts: List<GeminiPart>)
data class GeminiPart(val text: String)

data class GeminiResponse(val candidates: List<GeminiCandidate> = emptyList())
data class GeminiCandidate(val content: GeminiContent? = null)

fun GeminiResponse.firstText(): String =
    candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        ?: "No response was generated. Please try rephrasing your question."