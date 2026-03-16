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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.insight_android.TargetApp
import com.example.insight_android.model.SpModel
import com.example.insight_android.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpContentScreen(
    filePath: String,
    onBack: () -> Unit,
    onNavigateToDetail: (spName: String, key: String) -> Unit
) {
    val context = LocalContext.current
    val file = File(filePath)
    val spName = file.name.substringBeforeLast(".")

    var entries by remember { mutableStateOf<List<Map.Entry<String, Any?>>?>(null) }

    LaunchedEffect(filePath) {
        entries = withContext(Dispatchers.IO) {
            SpModel.loadSpContent(TargetApp.getTargetContext(context), spName).entries.toList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(spName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (entries == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    text = "Path: ${file.absolutePath}\nEntries: ${entries!!.size}\nSize: ${Utilities.formatFileLength(file.length())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (entries!!.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Empty SharedPreferences")
                    }
                } else {
                    LazyColumn {
                        itemsIndexed(entries!!) { index, entry ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "${index + 1}. ${entry.key}",
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                supportingContent = {
                                    val valueStr = entry.value.toString()
                                    Text(
                                        text = if (valueStr.length > 100) valueStr.take(100) + "..." else valueStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onNavigateToDetail(spName, entry.key)
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
