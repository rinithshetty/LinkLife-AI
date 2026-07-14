package com.lifelink.feature.medical

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.lifelink.core.ai.GeminiRepository
import com.lifelink.core.common.LifeLinkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OcrScannerUiState(
    val isProcessing: Boolean = false,
    val rawRecognizedText: String? = null,
    val aiAnalysis: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class OcrScannerViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrScannerUiState())
    val uiState: StateFlow<OcrScannerUiState> = _uiState.asStateFlow()

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processCapturedImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            _uiState.value = _uiState.value.copy(errorMessage = "Couldn't read the captured image. Try again.")
            return
        }

        _uiState.value = _uiState.value.copy(isProcessing = true, errorMessage = null)
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText -> onTextRecognized(visionText.text) }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    errorMessage = "Text recognition failed: ${e.message}",
                )
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun onTextRecognized(rawText: String) {
        if (rawText.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                errorMessage = "No text was detected. Try again with better lighting or a closer shot.",
            )
            return
        }

        _uiState.value = _uiState.value.copy(rawRecognizedText = rawText)

        viewModelScope.launch {
            when (val result = geminiRepository.analyzeOcrText(rawText)) {
                is LifeLinkResult.Success -> _uiState.value = _uiState.value.copy(isProcessing = false, aiAnalysis = result.data)
                is LifeLinkResult.Error -> _uiState.value = _uiState.value.copy(isProcessing = false, errorMessage = result.message)
                LifeLinkResult.Loading -> Unit
            }
        }
    }

    fun reset() {
        _uiState.value = OcrScannerUiState()
    }
}