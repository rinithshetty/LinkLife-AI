package com.lifelink.feature.medical

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.EmptyState
import com.lifelink.core.ui.components.MedicalDisclaimerBanner
import com.lifelink.core.ui.components.VerticalSpace
import androidx.compose.runtime.LaunchedEffect
import androidx.concurrent.futures.await


@Composable
fun OcrScannerScreen(onBack: () -> Unit, viewModel: OcrScannerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
    }
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Scanner") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MedicalDisclaimerBanner()

            when {
                !hasCameraPermission -> EmptyState("Camera permission is needed to scan medicine labels.")

                uiState.aiAnalysis != null -> OcrResultView(
                    rawText = uiState.rawRecognizedText.orEmpty(),
                    analysis = uiState.aiAnalysis.orEmpty(),
                    onScanAnother = { viewModel.reset() },
                )

                else -> Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreviewWithCapture(
                        onImageCaptured = { imageProxy -> viewModel.processCapturedImage(imageProxy) },
                    )
                    if (uiState.isProcessing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.errorMessage?.let { message ->
                        Card(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter)) {
                            Text(message, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewWithCapture(onImageCaptured: (ImageProxy) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(previewView) {
        val view = previewView ?: return@LaunchedEffect
        val cameraProvider = ProcessCameraProvider.getInstance(context).await()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(view.surfaceProvider)
        }
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture,
            )
        } catch (e: Exception) {
            android.util.Log.e("OcrScannerScreen", "Camera bind failed", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx -> PreviewView(ctx).also { previewView = it } },
        )

        FloatingActionButton(
            onClick = {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            onImageCaptured(image)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            android.util.Log.e("OcrScannerScreen", "Capture failed", exception)
                        }
                    },
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Capture medicine label")
        }
    }
}

@Composable
private fun OcrResultView(rawText: String, analysis: String, onScanAnother: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AI Analysis", style = MaterialTheme.typography.titleLarge)
        VerticalSpace(8)
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(analysis, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
        }
        VerticalSpace(16)
        Text("Raw scanned text", style = MaterialTheme.typography.titleMedium)
        VerticalSpace(8)
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(rawText, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
        }
        VerticalSpace(16)
        androidx.compose.material3.TextButton(onClick = onScanAnother) {
            Text("Scan another")
        }
    }
}