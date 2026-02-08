package com.bed1rock.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bed1rock.app.model.OperationState

@Composable
fun StatusOverlay(state: OperationState, onDismiss: () -> Unit) {
    when (state) {
        is OperationState.Progress -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                title = { Text("Processing...") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message)
                        Spacer(modifier = Modifier.height(16.dp))
                        state.percentage?.let {
                            LinearProgressIndicator(progress = it, modifier = Modifier.fillMaxWidth())
                        } ?: CircularProgressIndicator()
                    }
                }
            )
        }
        is OperationState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
                title = { Text("Error") },
                text = { Text(state.error) }
            )
        }
        is OperationState.Success -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = { TextButton(onClick = onDismiss) { Text("Great") } },
                title = { Text("Success") },
                text = { Text(state.message) }
            )
        }
        else -> {}
    }
}