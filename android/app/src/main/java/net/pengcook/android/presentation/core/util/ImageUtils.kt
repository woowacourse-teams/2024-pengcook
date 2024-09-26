package net.pengcook.android.presentation.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageUtils(
    private val context: Context,
) {
    private fun createTempImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat(DATA_FORMAT, Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            FILE_SUFFIX,
            storageDir,
        )
    }

    fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat(DATA_FORMAT, Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(null)
        return File
            .createTempFile(
                "JPEG_${timeStamp}_",
                FILE_SUFFIX,
                storageDir,
            )
    }

    fun getUriForFile(file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "net.pengcook.android.fileprovider",
            file,
        )

//    fun processImageUri(uri: Uri): String? =
//        try {
//            val inputStream = context.contentResolver.openInputStream(uri)
//            if (inputStream != null) {
//                val tempFile = createTempImageFile()
//                tempFile.outputStream().use { outputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//                tempFile.absolutePath
//            } else {
//                null
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }

    fun isPermissionGranted(permissions: Array<String>): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    suspend fun compressAndResizeImage(uri: Uri): File =
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            val resizedBitmap =
                adjustImageOrientation(
                    Bitmap.createScaledBitmap(
                        originalBitmap,
                        MAX_WIDTH,
                        MAX_HEIGHT,
                        true,
                    ),
                    uri,
                )

            val compressedFile = createTempImageFile()
            val outputStream = FileOutputStream(compressedFile)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()

            originalBitmap.recycle()
            resizedBitmap.recycle()

            compressedFile
        }

    private fun adjustImageOrientation(
        bitmap: Bitmap,
        uri: Uri,
    ): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exif = ExifInterface(inputStream!!)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    companion object {
        private const val COMPRESSED_QUALITY = 80
        private const val MAX_WIDTH = 1080
        private const val DATA_FORMAT = "yyyyMMdd_HHmmss"
        private const val FILE_SUFFIX = ".jpg"
        private const val MAX_HEIGHT = 1080
    }
}
