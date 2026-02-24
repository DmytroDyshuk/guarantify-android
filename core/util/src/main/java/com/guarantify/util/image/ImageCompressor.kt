package com.guarantify.util.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.guarantify.common.result.Result
import com.guarantify.common.result.Result.Error
import com.guarantify.common.result.Result.Success
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val maxSize = 1024f
    private val quality = 80

    fun compress(imageUri: Uri): Result<ByteArray> {
        var bitmap: Bitmap? = null
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(imageUri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            options.inSampleSize = calculateInSampleSize(options, maxSize.toInt(), maxSize.toInt())
            options.inJustDecodeBounds = false

            bitmap = context.contentResolver.openInputStream(imageUri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            } ?: return Error(IllegalStateException("Failed to decode bitmap"))

            bitmap = rotateImageIfRequired(bitmap, imageUri)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            Success(outputStream.toByteArray())
        } catch (e: Exception) {
            Log.e("ImageCompressor", "Failed to compress image", e)
            Error(e)
        } finally {
            bitmap?.recycle()
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun rotateImageIfRequired(img: Bitmap, uri: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(uri) ?: return img
        return input.use {
            val exifInterface = ExifInterface(it)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
                else -> img
            }
            rotatedBitmap
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        if (rotatedImg != img) img.recycle()
        return rotatedImg
    }
}