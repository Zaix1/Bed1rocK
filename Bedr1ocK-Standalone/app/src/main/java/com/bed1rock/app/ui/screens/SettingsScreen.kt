package com.bed1rock.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bed1rock.app.model.ThemeMode
import com.bed1rock.app.ui.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState(initial = com.bed1rock.app.model.AppSettings())

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Column {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dynamic Color", Modifier.weight(1f))
                Switch(checked = settings.useDynamicColor, onCheckedChange = { viewModel.updateDynamicColor(it) })
            }
        }

        Column {
            Text("Theme Mode", style = MaterialTheme.typography.titleMedium)
            ThemeMode.values().forEach { mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = settings.themeMode == mode, onClick = { viewModel.updateTheme(mode) })
                    Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
        
        HorizontalDivider()
        Text("Bed1rock v1.0.0 | Offline Mode", style = MaterialTheme.typography.labelSmall)
    }
}