package com.bed1rock.app.model

import android.net.Uri

sealed class OperationState {
    object Idle : OperationState()
    data class Progress(val message: String, val percentage: Float? = null) : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val error: String) : OperationState()
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

data class Manifest(
    val header: Header
) {
    data class Header(val name: String, val type: String)
}