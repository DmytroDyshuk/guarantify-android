package com.guarantify.data.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.guarantify.domain.model.StorageError
import com.guarantify.domain.repository.WarrantiesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.work.ListenableWorker.Result as WorkResult
import com.guarantify.common.result.Result as CommonResult

@HiltWorker
class UploadImageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val warrantiesRepository: WarrantiesRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): WorkResult {
        val warrantyId = inputData.getString(WorkerKeys.KEY_WARRANTY_ID)
        val compressedImageUri = inputData.getString(WorkerKeys.KEY_COMPRESSED_IMAGE_URI)

        if (warrantyId.isNullOrBlank() || compressedImageUri.isNullOrBlank()) {
            Log.w("UploadImageWorker", "Missing required input data")
            return WorkResult.failure()
        }

        return when (val result =
            warrantiesRepository.uploadWarrantyPhoto(warrantyId, compressedImageUri)) {

            is CommonResult.Success -> {
                val outputData = workDataOf(
                    WorkerKeys.KEY_UPLOADED_IMAGE_URL to result.data,
                    WorkerKeys.KEY_WARRANTY_ID to warrantyId
                )

                WorkResult.success(outputData)
            }

            is CommonResult.Error -> {
                val error = result.throwable
                Log.e("UploadImageWorker", "Upload failed: $error")

                when (error) {
                    is StorageError.NetworkError,
                    is StorageError.QuotaExceeded -> WorkResult.retry()

                    is StorageError.Unauthorized -> {
                        WorkResult.failure()
                    }

                    else -> WorkResult.failure()
                }
            }
        }
    }

}