package com.guarantify.data.workmanager

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.FirebaseException
import com.guarantify.common.di.IoDispatcher
import com.guarantify.data.network.firebase.storage.WarrantyPhotoStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import androidx.work.ListenableWorker.Result as WorkResult
import com.guarantify.common.result.Result as CommonResult

@HiltWorker
class UploadImageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val warrantyPhotoStorage: WarrantyPhotoStorage,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): WorkResult {
        val warrantyId =
            inputData.getString(WorkerKeys.KEY_WARRANTY_ID) ?: return WorkResult.failure()
        val compressedImageUri =
            inputData.getString(WorkerKeys.KEY_COMPRESSED_IMAGE_URI) ?: return WorkResult.failure()

        val uploadResult = withContext(ioDispatcher) {
            warrantyPhotoStorage.uploadImage(compressedImageUri.toUri(), warrantyId)
        }

        return when (uploadResult) {
            is CommonResult.Success -> {
                val outputData = workDataOf(
                    WorkerKeys.KEY_UPLOADED_IMAGE_URL to uploadResult.data,
                    WorkerKeys.KEY_WARRANTY_ID to warrantyId
                )
                WorkResult.success(outputData)
            }
            is CommonResult.Error -> {
                val exception = uploadResult.throwable
                Log.e("UploadImageWorker", "Upload failed for $warrantyId", exception)

                when (exception) {
                    is IOException -> {
                        WorkResult.retry()
                    }

                    is FirebaseException -> {
                        if (isRecoverable(exception)) {
                            WorkResult.retry()
                        } else {
                            WorkResult.failure()
                        }
                    }

                    else -> WorkResult.failure()
                }
            }
        }
    }

    private fun isRecoverable(e: FirebaseException): Boolean {
        val msg = e.message?.lowercase() ?: ""
        return msg.contains("retry-limit-exceeded") || msg.contains("quota-exceeded")
    }

}