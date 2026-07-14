package com.lifelink.feature.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.EmptyState
import com.lifelink.core.ui.components.MedicalDisclaimerBanner

/**
 * FR-7.3: if the Gemini call fails (most commonly: no connectivity), the ViewModel
 * surfaces a clear inline message rather than crashing or showing a spinner forever —
 * this screen never needs its own offline branch beyond showing that message.
 */
@Composable
fun AssistantScreen(onBack: () -> Unit, viewModel: AssistantViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Symptom Explainer") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MedicalDisclaimerBanner()

            if (uiState.messages.isEmpty()) {
                EmptyState(
                    "Describe how you're feeling — for example, \"I have a mild headache and feel tired.\"",
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.messages) { message ->
                        Card(
                            colors = if (message.isUser) {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            } else {
                                CardDefaults.cardColors()
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        ) {
                            Text(message.text.toBoldAnnotatedString(), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Describe your symptoms") },
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            viewModel.sendMessage(input)
                            input = ""
                        }
                    },
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}
/**
 * Minimal markdown support: renders `**bold**` segments as real bold spans instead of
 * literal asterisks. Not a full markdown renderer — Gemini's responses here are short,
 * plain-language explanations where bold on key terms is the only formatting it tends
 * to use, so a full markdown library would be overkill for this one screen.
 */
private fun String.toBoldAnnotatedString() = buildAnnotatedString {
    val boldMarker = "**"
    var remaining = this@toBoldAnnotatedString
    while (true) {
        val start = remaining.indexOf(boldMarker)
        if (start == -1) {
            append(remaining)
            break
        }
        val end = remaining.indexOf(boldMarker, start + boldMarker.length)
        if (end == -1) {
            append(remaining)
            break
        }
        append(remaining.substring(0, start))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(remaining.substring(start + boldMarker.length, end))
        }
        remaining = remaining.substring(end + boldMarker.length)
    }
}
