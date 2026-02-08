package com.bed1rock.app.domain

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.bed1rock.app.model.PackMetadata
import com.bed1rock.app.model.PackType
import org.json.JSONObject
import java.io.InputStream

class PackParser(private val context: Context) {

    /**
     * Entry point for detecting a pack's identity.
     * Uses manifest parsing with structural fallback.
     */
    fun parsePack(folder: DocumentFile): PackMetadata {
        val manifestFile = folder.findFile("manifest.json")
        
        return if (manifestFile != null) {
            try {
                parseManifest(manifestFile)
            } catch (e: Exception) {
                fallbackDetection(folder) // Parser failed, try structure
            }
        } else {
            fallbackDetection(folder) // No manifest, try structure
        }
    }

    private fun parseManifest(file: DocumentFile): PackMetadata {
        val jsonString = context.contentResolver.openInputStream(file.uri)?.use { 
            it.bufferedReader().readText() 
        } ?: throw Exception("Read error")

        val root = JSONObject(jsonString)
        val header = root.optJSONObject("header")
        val modules = root.optJSONArray("modules")

        val name = header?.optString("name") ?: "Unknown Pack"
        val uuid = header?.optString("uuid") ?: ""

        // Reliable detection via modules[].type
        var detectedType = PackType.UNKNOWN
        if (modules != null && modules.length() > 0) {
            for (i in 0 until modules.length()) {
                val module = modules.getJSONObject(i)
                val typeStr = module.optString("type")
                when (typeStr) {
                    "resources", "client_data" -> detectedType = PackType.RESOURCE
                    "data", "javascript", "script" -> detectedType = PackType.BEHAVIOR
                }
                if (detectedType != PackType.UNKNOWN) break
            }
        }

        return PackMetadata(name, detectedType, uuid)
    }

    /**
     * Heuristic analysis based on folder presence.
     * Essential for malformed manifests common in the community.
     */
    private fun fallbackDetection(folder: DocumentFile): PackMetadata {
        val resourceFolders = listOf("textures", "models", "sounds", "particles", "ui")
        val behaviorFolders = listOf("entities", "functions", "loot_tables", "recipes", "scripts")

        val contents = folder.listFiles().mapNotNull { it.name }
        
        val resMatch = contents.count { it in resourceFolders }
        val behMatch = contents.count { it in behaviorFolders }

        val type = when {
            resMatch > behMatch -> PackType.RESOURCE
            behMatch > resMatch -> PackType.BEHAVIOR
            else -> PackType.UNKNOWN
        }

        return PackMetadata(folder.name ?: "Unknown", type)
    }
}