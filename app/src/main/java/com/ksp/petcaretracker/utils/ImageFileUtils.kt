package com.ksp.petcaretracker.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ImageFileUtils {
    fun createImageUri(context: Context): Uri {
        val imageDir = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "pet_images").apply {
            if (!exists()) mkdirs()
        }
        val imageFile = File(imageDir, "pet_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    }
}
