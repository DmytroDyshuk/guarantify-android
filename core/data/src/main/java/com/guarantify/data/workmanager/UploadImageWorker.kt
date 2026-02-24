package com.guarantify.data.workmanager

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.guarantify.common.di.IoDispatcher
import com.guarantify.data.network.firebase.storage.WarrantyPhotoStorage
import com.guarantify.domain.model.FirebaseStorageError
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
        val warrantyId = inputData.getString(WorkerKeys.KEY_WARRANTY_ID)
        val compressedImageUri = inputData.getString(WorkerKeys.KEY_COMPRESSED_IMAGE_URI)

        if (warrantyId.isNullOrBlank() || compressedImageUri.isNullOrBlank()) {
            Log.w("UploadImageWorker", "Missing required input data")
            return WorkResult.failure()
        }

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
                val error = uploadResult.throwable
                Log.e("UploadImageWorker", "Upload failed: $error")

                when (error) {
                    is FirebaseStorageError.NetworkError,
                    is FirebaseStorageError.QuotaExceeded -> WorkResult.retry()

                    is FirebaseStorageError.Unauthorized -> {
                        WorkResult.failure()
                    }

                    else -> WorkResult.failure()
                }
            }
        }
    }

}