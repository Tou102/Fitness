
package com.example.fitness.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

suspend fun copyUriToInternalStorage(context: Context, uri: Uri, filename: String): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val file = File(context.filesDir, filename)
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file.absolutePath  // Trả về đường dẫn file
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
