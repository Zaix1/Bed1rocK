package com.bed1rock.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bed1rock.app.ui.MainViewModel

@Composable
fun WorldImportScreen(viewModel: MainViewModel) {
    var psUri by remember { mutableStateOf<Uri?>(null) }
    var sourceUri by remember { mutableStateOf<Uri?>(null) }
    val opState by viewModel.opState.collectAsState()

    val psLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { psUri = it }
    val sourceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { sourceUri = it }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("World Replacement", style = MaterialTheme.typography.headlineMedium)
        
        OutlinedButton(onClick = { psLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
            Text(psUri?.path?.takeLast(30) ?: "Select Decrypted PS Save Folder")
        }

        OutlinedButton(onClick = { sourceLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
            Text(sourceUri?.path?.takeLast(30) ?: "Select Custom World Folder")
        }

        Button(
            onClick = { viewModel.replaceWorld(psUri!!, sourceUri!!, false) },
            enabled = psUri != null && sourceUri != null,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Replace World")
        }

        Text(
            "Note: This will delete the existing world inside the save but keep the 'sce_sys' folder safe.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }

    StatusOverlay(opState, onDismiss = { viewModel.resetState() })
}