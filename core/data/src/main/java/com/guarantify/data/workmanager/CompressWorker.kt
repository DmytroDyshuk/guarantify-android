package com.guarantify.data.workmanager

import android.content.Context
import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import androidx.work.ListenableWorker.Result as WorkResult
import com.guarantify.common.result.Result as CommonResult

@HiltWorker
class CompressWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val imageCompressor: ImageCompressor,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): WorkResult {
        return withContext(ioDispatcher) {
            try {
                val inputData = validateInputData() ?: return@withContext WorkResult.failure()

                when (val compressResult = imageCompressor.compress(inputData.imageUri)) {
                    is CommonResult.Success -> {
                        val compressedImageUriString = saveCompressedImage(
                            compressResult.data,
                            inputData.warrantyId
                        )
                        WorkResult.success(
                            createOutputData(compressedImageUriString, inputData.warrantyId)
                        )
                    }

                    is CommonResult.Error -> {
                        handleError(compressResult.throwable)
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: OutOfMemoryError) {
                WorkResult.retry()
            } catch (e: Exception) {
                Log.e("CompressWorker", "Failed to compress image", e)
                handleError(e)
            }
        }
    }

    private fun validateInputData(): ImageCompressionData? {
        val imageUriString = inputData.getString(KEY_IMAGE_URI)
        val warrantyId = inputData.getString(KEY_WARRANTY_ID)

        return if (imageUriString.isNullOrEmpty() || warrantyId.isNullOrEmpty()) {
            null
        } else ImageCompressionData(imageUriString.toUri(), warrantyId)
    }

    private fun saveCompressedImage(compressedBytes: ByteArray, warrantyId: String): String {
        val cacheDir = applicationContext.cacheDir
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val fileName = "warranty_${warrantyId}.jpg"
        val compressedFile = File(cacheDir, fileName)
        compressedFile.writeBytes(compressedBytes)

        return compressedFile.toUri().toString()
    }

    private fun createOutputData(compressedUri: String, warrantyId: String) =
        workDataOf(
            KEY_COMPRESSED_IMAGE_URI to compressedUri,
            KEY_WARRANTY_ID to warrantyId
        )

    private fun handleError(throwable: Throwable?): WorkResult {
        return when (throwable) {
            is IOException, is SecurityException -> WorkResult.retry()
            is IllegalStateException -> WorkResult.failure()
            else -> WorkResult.failure()
        }
    }

    private data class ImageCompressionData(
        val imageUri: Uri,
        val warrantyId: String
    )
}