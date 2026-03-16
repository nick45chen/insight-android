package com.example.insight_android.screen

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
import com.example.insight_android.model.DbModel
import com.example.insight_android.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbListScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var data by remember { mutableStateOf<DbModel.DbListData?>(null) }

    LaunchedEffect(Unit) {
        data = withContext(Dispatchers.IO) {
            DbModel.loadDbListData(TargetApp.getTargetContext(context))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Database") },
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
                    text = "Path: ${data!!.dir.absolutePath}\nDatabases: ${data!!.files.size}\nSize: ${Utilities.formatFileLength(data!!.totalLength)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (data!!.files.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No database files found")
                    }
                } else {
                    LazyColumn {
                        itemsIndexed(data!!.files) { index, dbFile ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "${index + 1}. ${dbFile.name}",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                trailingContent = {
                                    Text(
                                        Utilities.formatFileLength(dbFile.length()),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
