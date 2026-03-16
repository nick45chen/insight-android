package com.example.insight_android.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insight_android.TargetApp
import com.example.insight_android.model.SpModel
import com.example.insight_android.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpListScreen(
    onBack: () -> Unit,
    onNavigateToContent: (path: String) -> Unit
) {
    val context = LocalContext.current
    var data by remember { mutableStateOf<SpModel.SpListData?>(null) }

    LaunchedEffect(Unit) {
        data = withContext(Dispatchers.IO) {
            SpModel.loadSpListData(TargetApp.getTargetContext(context))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SharedPreferences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (data == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    text = "Files: ${data!!.files.size}, Size: ${Utilities.formatFileLength(data!!.totalLength)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (data!!.files.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No SharedPreferences files found")
                    }
                } else {
                    LazyColumn {
                        itemsIndexed(data!!.files) { index, spFile ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "${index + 1}. ${spFile.file.name.substringBeforeLast(".")}",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                trailingContent = {
                                    Text(
                                        Utilities.formatFileLength(spFile.size),
                                        color = if (spFile.size > 50 * 1024)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onNavigateToContent(spFile.file.absolutePath)
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
