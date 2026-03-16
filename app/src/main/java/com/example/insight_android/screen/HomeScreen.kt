package com.example.insight_android.screen

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insight_android.TargetApp
import com.example.insight_android.model.*
import com.example.insight_android.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSp: () -> Unit,
    onNavigateToDb: () -> Unit,
    onNavigateToFiles: (title: String, path: String) -> Unit
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    // Data states
    var processInfo by remember { mutableStateOf("") }
    var internalStorageInfo by remember { mutableStateOf("") }
    var externalStorageInfo by remember { mutableStateOf<String?>(null) }
    var spInfo by remember { mutableStateOf("") }
    var dbInfo by remember { mutableStateOf("") }
    var internalStoragePath by remember { mutableStateOf("") }
    var externalStoragePath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        errorMessage = null

        // Check if target app is installed
        val installed = try {
            context.packageManager.getPackageInfo(TargetApp.TARGET_PACKAGE_NAME, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }

        if (!installed) {
            errorMessage = "${TargetApp.TARGET_PACKAGE_NAME} is not installed"
            isLoading = false
            return@LaunchedEffect
        }

        val targetContext = try {
            TargetApp.getTargetContext(context)
        } catch (e: Exception) {
            errorMessage = "Cannot access target app: ${e.message}"
            isLoading = false
            return@LaunchedEffect
        }

        val dataDir = targetContext.filesDir.parentFile!!
        if (dataDir.list() == null) {
            errorMessage = "Cannot read target app files.\nEnsure both apps share the same sharedUserId and signing key."
            isLoading = false
            return@LaunchedEffect
        }

        internalStoragePath = dataDir.absolutePath

        withContext(Dispatchers.IO) {
            // Process info
            val pid = TargetApp.getTargetPid(context)
            processInfo = if (pid == -1) {
                "${TargetApp.TARGET_PACKAGE_NAME} is not running"
            } else {
                val threads = ProcessModel.getThreadCount(pid)
                val fds = ProcessModel.getFdCount(pid)
                val mem = ProcessModel.getMemoryInfo(context, pid)
                "PID: $pid\nThreads: $threads\nFD count: $fds\nMemory (PSS): ${Utilities.formatMemorySize(mem.totalPss)}"
            }

            // Internal storage
            val internalData = FileModel.getFileSizeData(dataDir)
            internalStorageInfo = "Path: ${dataDir.absolutePath}\nFiles: ${internalData.count}\nSize: ${Utilities.formatFileLength(internalData.totalLength)}"

            // External storage
            try {
                val extDir = targetContext.getExternalFilesDir(null)?.parentFile
                if (extDir != null && extDir.exists()) {
                    externalStoragePath = extDir.absolutePath
                    val extData = FileModel.getFileSizeData(extDir)
                    externalStorageInfo = "Path: ${extDir.absolutePath}\nFiles: ${extData.count}\nSize: ${Utilities.formatFileLength(extData.totalLength)}"
                }
            } catch (_: Exception) {
                externalStorageInfo = null
            }

            // SharedPreferences
            val spData = SpModel.loadSpListData(targetContext)
            spInfo = "Path: ${spData.dir.absolutePath}\nFiles: ${spData.files.size}\nSize: ${Utilities.formatFileLength(spData.totalLength)}"

            // Database
            val dbData = DbModel.loadDbListData(targetContext)
            dbInfo = "Path: ${dbData.dir.absolutePath}\nDatabases: ${dbData.files.size}\nSize: ${Utilities.formatFileLength(dbData.totalLength)}"
        }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(TargetApp.TARGET_PACKAGE_NAME) },
                actions = {
                    IconButton(onClick = { refreshTrigger++ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Process Info
                CategoryCard(
                    title = "Process Info",
                    summary = processInfo,
                    showArrow = false,
                    onClick = {}
                )

                // Internal Storage
                CategoryCard(
                    title = "Internal Storage",
                    summary = internalStorageInfo,
                    onClick = { onNavigateToFiles("Internal Storage", internalStoragePath) }
                )

                // External Storage
                if (externalStorageInfo != null) {
                    CategoryCard(
                        title = "External Storage",
                        summary = externalStorageInfo!!,
                        onClick = { onNavigateToFiles("External Storage", externalStoragePath!!) }
                    )
                }

                // SharedPreferences
                CategoryCard(
                    title = "SharedPreferences",
                    summary = spInfo,
                    onClick = onNavigateToSp
                )

                // Database
                CategoryCard(
                    title = "Database",
                    summary = dbInfo,
                    onClick = onNavigateToDb
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    summary: String,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .then(if (showArrow) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (showArrow) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
