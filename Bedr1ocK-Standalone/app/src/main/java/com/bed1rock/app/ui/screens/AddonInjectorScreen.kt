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
fun AddonInjectorScreen(viewModel: MainViewModel) {
    var psUri by remember { mutableStateOf<Uri?>(null) }
    var addonUri by remember { mutableStateOf<Uri?>(null) }
    val opState by viewModel.opState.collectAsState()

    val psLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { psUri = it }
    // Accept .mcpack, .mcaddon, and .zip
    val addonLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { addonUri = it }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Add-On Injector", style = MaterialTheme.typography.headlineMedium)
        Text("Safely add Resource or Behavior packs to your world without losing progress.")

        OutlinedButton(onClick = { psLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
            Text(psUri?.lastPathSegment ?: "Select Decrypted PS Save Folder")
        }

        OutlinedButton(onClick = { addonLauncher.launch(arrayOf("application/zip", "application/octet-stream")) }, modifier = Modifier.fillMaxWidth()) {
            Text(addonUri?.lastPathSegment ?: "Select .mcpack / .mcaddon")
        }

        Button(
            onClick = { viewModel.injectAddon(psUri!!, addonUri!!) },
            enabled = psUri != null && addonUri != null,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Inject Add-On")
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(12.dp)) {
                Text("How it works:", style = MaterialTheme.typography.labelLarge)
                Text("• Auto-detects if it's a Texture Pack or Behavior Pack.", style = MaterialTheme.typography.bodySmall)
                Text("• Creates necessary folders if they don't exist.", style = MaterialTheme.typography.bodySmall)
                Text("• Never touches your level.dat or world DB.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    StatusOverlay(opState, onDismiss = { viewModel.resetState() })
}