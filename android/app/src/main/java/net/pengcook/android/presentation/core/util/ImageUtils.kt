package net.pengcook.android.presentation.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageUtils(private val context: Context) {
    private fun createTempImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir,
        )
    }

    fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(null)
        return File
            .createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir,
            )
    }

    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "net.pengcook.android.fileprovider",
            file,
        )
    }

    fun processImageUri(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val tempFile = createTempImageFile()
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile.absolutePath
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun isPermissionGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}