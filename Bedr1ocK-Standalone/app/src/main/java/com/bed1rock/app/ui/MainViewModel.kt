package com.bed1rock.app.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bed1rock.app.data.SettingsRepository
import com.bed1rock.app.domain.FileManager
import com.bed1rock.app.model.OperationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val fileManager: FileManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _opState = MutableStateFlow<OperationState>(OperationState.Idle)
    val opState: StateFlow<OperationState> = _opState

    val settings = settingsRepository.settingsFlow

    fun replaceWorld(psUri: Uri, sourceUri: Uri, isArchive: Boolean) {
        viewModelScope.launch {
            _opState.value = OperationState.Progress("Starting import...")
            val result = fileManager.replaceWorld(psUri, sourceUri, isArchive) { msg, p ->
                _opState.value = OperationState.Progress(msg, p)
            }
            _opState.value = result.fold(
                onSuccess = { OperationState.Success("World successfully replaced!") },
                onFailure = { OperationState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun injectAddon(psUri: Uri, addonUri: Uri) {
        viewModelScope.launch {
            _opState.value = OperationState.Progress("Injecting add-on...")
            val result = fileManager.injectAddon(psUri, addonUri) { msg, p ->
                _opState.value = OperationState.Progress(msg, p)
            }
            _opState.value = result.fold(
                onSuccess = { OperationState.Success("Add-on injected!") },
                onFailure = { OperationState.Error(it.message ?: "Injection failed") }
            )
        }
    }

    fun resetState() { _opState.value = OperationState.Idle }
    
    fun updateTheme(mode: com.bed1rock.app.model.ThemeMode) = viewModelScope.launch { settingsRepository.updateTheme(mode) }
    fun updateDynamicColor(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateDynamicColor(enabled) }
}