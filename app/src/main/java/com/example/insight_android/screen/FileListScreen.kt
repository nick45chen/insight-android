package com.example.insight_android.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insight_android.model.FileModel
import com.example.insight_android.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(
    title: String,
    rootPath: String,
    onBack: () -> Unit,
    onNavigateToFileText: (path: String) -> Unit
) {
    val rootDir = remember { File(rootPath) }
    var currentDir by remember { mutableStateOf(rootDir) }
    var data by remember { mutableStateOf<FileModel.DirSizeData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentDir) {
        isLoading = true
        data = withContext(Dispatchers.IO) {
            FileModel.getDirSizeData(currentDir)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentDir != rootDir) {
                            currentDir = currentDir.parentFile ?: rootDir
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading || data == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    text = "Items: ${data!!.list.size}, Files: ${data!!.dirData.count}, Size: ${Utilities.formatFileLength(data!!.dirData.totalLength)}\nPath: ${data!!.dirData.file.absolutePath}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (currentDir != rootDir) {
                    ListItem(
                        headlineContent = {
                            Text(".. (parent directory)", fontWeight = FontWeight.Bold)
                        },
                        leadingContent = {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier.clickable {
                            currentDir = currentDir.parentFile ?: rootDir
                        }
                    )
                    HorizontalDivider()
                }

                if (data!!.list.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Empty directory")
                    }
                } else {
                    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
                    LazyColumn {
                        itemsIndexed(data!!.list) { index, item ->
                            val isDir = item.file.isDirectory
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "${index + 1}. ${item.file.name}",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                supportingContent = {
                                    val info = buildString {
                                        append(dateFormat.format(Date(item.file.lastModified())))
                                        if (isDir) append(" - Files: ${item.count}")
                                        append(" - ${Utilities.formatFileLength(item.totalLength)}")
                                    }
                                    Text(info)
                                },
                                leadingContent = {
                                    Icon(
                                        if (isDir) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                                        contentDescription = null,
                                        tint = if (isDir) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier.clickable {
                                    if (isDir) {
                                        currentDir = item.file
                                    } else if (item.totalLength < 200 * 1024) {
                                        onNavigateToFileText(item.file.absolutePath)
                                    }
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
