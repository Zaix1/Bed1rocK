package com.bed1rock.app.domain

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.bed1rock.app.model.PackType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipInputStream

class FileManager(private val context: Context) {

    private val parser = PackParser(context)

    // --- WORLD REPLACEMENT ---
    suspend fun replaceWorld(
        psSaveUri: Uri,
        sourceUri: Uri,
        isArchive: Boolean,
        onProgress: (String, Float?) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val psFolder = DocumentFile.fromTreeUri(context, psSaveUri)
                ?: return@withContext Result.failure(Exception("Invalid PS Folder"))
            val savedata0 = psFolder.findFile("savedata0")
                ?: return@withContext Result.failure(Exception("Missing 'savedata0' directory"))

            if (savedata0.findFile("sce_sys") == null)
                return@withContext Result.failure(Exception("CRITICAL ERROR: sce_sys not found!"))

            onProgress("Cleaning old world data...", 0.1f)
            savedata0.listFiles().forEach { if (it.name != "sce_sys") it.delete() }

            onProgress("Importing new world...", 0.4f)
            if (isArchive) extractZipToDocument(sourceUri, savedata0)
            else {
                DocumentFile.fromTreeUri(context, sourceUri)?.listFiles()?.forEach { copyDocument(it, savedata0) }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ADDON INJECTION ---
    suspend fun injectAddon(
        psSaveUri: Uri,
        addonSourceUri: Uri,
        onProgress: (String, Float?) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val psRoot = DocumentFile.fromTreeUri(context, psSaveUri)
            val savedata0 = psRoot?.findFile("savedata0")
                ?: return@withContext Result.failure(Exception("Invalid PlayStation save structure"))

            onProgress("Analyzing pack structure...", 0.1f)
            val tempDir = File(context.cacheDir, "addon_extract_${System.currentTimeMillis()}")
            tempDir.mkdirs()
            extractUriToCache(addonSourceUri, tempDir)
            val extractedDoc = DocumentFile.fromFile(tempDir)

            val metadata = parser.parsePack(extractedDoc)
            if (metadata.type == PackType.UNKNOWN)
                return@withContext Result.failure(Exception("Could not determine pack type"))

            val targetDirName = if (metadata.type == PackType.RESOURCE) "resource_packs" else "behavior_packs"
            val targetFolder = savedata0.findFile(targetDirName) ?: savedata0.createDirectory(targetDirName)

            onProgress("Injecting ${metadata.name} into $targetDirName...", 0.5f)
            val packFinalFolder = targetFolder!!.createDirectory(metadata.name.filter { it.isLetterOrDigit() })
            copyFolderContents(extractedDoc, packFinalFolder!!)

            tempDir.deleteRecursively()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- HELPERS ---
    private fun extractUriToCache(uri: Uri, target: File) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val file = File(target, entry.name)
                    if (entry.isDirectory) file.mkdirs() else {
                        file.parentFile?.mkdirs()
                        file.outputStream().use { zis.copyTo(it) }
                    }
                    entry = zis.nextEntry
                }
            }
        }
    }

    private fun extractZipToDocument(zipUri: Uri, targetFolder: DocumentFile) {
        context.contentResolver.openInputStream(zipUri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val file = targetFolder.createFile("", entry.name ?: "file")
                        context.contentResolver.openOutputStream(file!!.uri)?.use { output ->
                            zis.copyTo(output)
                        }
                    }
                    entry = zis.nextEntry
                }
            }
        }
    }

    private fun copyFolderContents(source: DocumentFile, target: DocumentFile) {
        source.listFiles().forEach { item ->
            if (item.isDirectory) {
                val newDir = target.createDirectory(item.name!!)
                copyFolderContents(item, newDir!!)
            } else {
                val newFile = target.createFile(item.type ?: "application/octet-stream", item.name!!)
                context.contentResolver.openInputStream(item.uri)?.use { input ->
                    context.contentResolver.openOutputStream(newFile!!.uri)?.use { output -> input.copyTo(output) }
                }
            }
        }
    }

    private fun copyDocument(source: DocumentFile, targetParent: DocumentFile) {
        if (source.isDirectory) {
            val newDir = targetParent.createDirectory(source.name!!)
            source.listFiles().forEach { copyDocument(it, newDir!!) }
        } else {
            val newFile = targetParent.createFile(source.type ?: "application/octet-stream", source.name!!)
            context.contentResolver.openInputStream(source.uri)?.use { input ->
                context.contentResolver.openOutputStream(newFile!!.uri)?.use { output -> input.copyTo(output) }
            }
        }
    }
}
