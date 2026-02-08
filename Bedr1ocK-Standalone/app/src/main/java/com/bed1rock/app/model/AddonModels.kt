package com.bed1rock.app.model

enum class PackType {
    RESOURCE,
    BEHAVIOR,
    UNKNOWN
}

data class PackMetadata(
    val name: String,
    val type: PackType,
    val uuid: String = "",
    val version: String = "1.0.0"
)