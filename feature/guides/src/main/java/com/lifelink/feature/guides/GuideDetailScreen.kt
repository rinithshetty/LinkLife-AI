package com.lifelink.feature.guides

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.EmptyState

@Composable
fun GuideDetailScreen(guideId: String, onBack: () -> Unit, viewModel: GuideDetailViewModel = hiltViewModel()) {
    val guide by viewModel.guide.collectAsState()

    LaunchedEffect(guideId) { viewModel.load(guideId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(guide?.title ?: "Guide") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        val currentGuide = guide
        if (currentGuide == null) {
            EmptyState("Loading…", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                items(currentGuide.steps.size) { index ->
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("${index + 1}.", style = MaterialTheme.typography.titleMedium)
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(currentGuide.steps[index], style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
