package com.example.insight_android.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insight_android.TargetApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpDetailScreen(
    spName: String,
    key: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var valueStr by remember { mutableStateOf<String?>(null) }
    var typeName by remember { mutableStateOf("") }

    LaunchedEffect(spName, key) {
        withContext(Dispatchers.IO) {
            val targetContext = TargetApp.getTargetContext(context)
            val sp = targetContext.getSharedPreferences(spName, Context.MODE_PRIVATE)
            val value = sp.all[key]
            typeName = when (value) {
                is Int -> "Int"
                is Long -> "Long"
                is Float -> "Float"
                is Boolean -> "Boolean"
                is Set<*> -> "StringSet"
                else -> "String"
            }
            valueStr = value.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(key) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (valueStr != null) {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(valueStr!!))
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (valueStr == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Type: $typeName",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (typeName != "Boolean") {
                    Text(
                        text = "Length: ${valueStr!!.length}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Value:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                SelectionContainer {
                    Text(
                        text = valueStr!!,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
