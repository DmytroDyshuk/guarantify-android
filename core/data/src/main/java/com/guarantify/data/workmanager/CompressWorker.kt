package com.guarantify.data.workmanager

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.guarantify.common.di.IoDispatcher
import com.guarantify.data.workmanager.WorkerKeys.KEY_COMPRESSED_IMAGE_URI
import com.guarantify.data.workmanager.WorkerKeys.KEY_IMAGE_URI
import com.guarantify.data.workmanager.WorkerKeys.KEY_WARRANTY_ID
import com.guarantify.util.image.ImageCompressor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class CompressWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val imageCompressor: ImageCompressor,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val imageUriString = inputData.getString(KEY_IMAGE_URI) ?: return Result.failure()
            val warrantyId = inputData.getString(KEY_WARRANTY_ID) ?: return Result.failure()
            val imageUri = imageUriString.toUri()

            val compressedImageUriString = withContext(ioDispatcher) {
                val compressedBytes = imageCompressor.compress(imageUri) ?: return@withContext null

                val compressedFile = File(applicationContext.cacheDir, "warranty_${warrantyId}.jpg")
                compressedFile.writeBytes(compressedBytes)
                Uri.fromFile(compressedFile).toString()
            } ?: return Result.failure()

            Result.success(
                workDataOf(
                    KEY_COMPRESSED_IMAGE_URI to compressedImageUriString,
                    KEY_WARRANTY_ID to warrantyId
                )
            )
        } catch (_: Exception) {
            Result.failure()
        }
    }
}