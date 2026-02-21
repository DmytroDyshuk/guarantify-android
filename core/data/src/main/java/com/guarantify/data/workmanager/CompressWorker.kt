package com.guarantify.data.workmanager

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.guarantify.util.image.ImageCompressor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class CompressWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val imageCompressor: ImageCompressor
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val imageUriString = inputData.getString(KEY_IMAGE_URI) ?: return Result.failure()
            val imageUri = imageUriString.toUri()

            val compressedImageUriString = withContext(Dispatchers.IO) {
                val compressedBytes = imageCompressor.compress(imageUri) ?: return@withContext null

                val compressedFile = File(applicationContext.cacheDir, "compressed_${id}.jpg")
                compressedFile.writeBytes(compressedBytes)
                Uri.fromFile(compressedFile).toString()
            } ?: return Result.failure()

            Result.success(workDataOf(KEY_COMPRESSED_IMAGE_URI to compressedImageUriString))
        } catch (_: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_IMAGE_URI = "imageUri"
        const val KEY_COMPRESSED_IMAGE_URI = "compressedImageUri"
    }
}