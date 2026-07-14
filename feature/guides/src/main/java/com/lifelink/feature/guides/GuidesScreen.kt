package com.lifelink.feature.guides

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lifelink.core.ui.components.EmptyState

/** FR-6: works fully offline — content is seeded from bundled JSON, never fetched. */
@Composable
fun GuidesScreen(onBack: () -> Unit, onOpenGuide: (String) -> Unit, viewModel: GuidesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Guides") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        if (uiState.guides.isEmpty()) {
            EmptyState("Loading offline guides…", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(uiState.guides, key = { it.id }) { guide ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        ListItem(
                            headlineContent = { Text(guide.title) },
                            supportingContent = { Text(guide.disasterType.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium) },
                            trailingContent = { Icon(Icons.Filled.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { onOpenGuide(guide.id) },
                        )
                    }
                }
            }
        }
    }
}
