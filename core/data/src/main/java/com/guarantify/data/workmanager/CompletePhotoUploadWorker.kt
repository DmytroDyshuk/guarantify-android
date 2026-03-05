package com.guarantify.data.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.guarantify.common.di.IoDispatcher
import com.guarantify.data.workmanager.WorkerKeys.KEY_UPLOADED_IMAGE_URL
import com.guarantify.data.workmanager.WorkerKeys.KEY_WARRANTY_ID
import com.guarantify.domain.repository.WarrantiesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import androidx.work.ListenableWorker.Result as WorkResult
import com.guarantify.common.result.Result as CommonResult

@HiltWorker
class CompletePhotoUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val warrantiesRepository: WarrantiesRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): WorkResult {
        return withContext(ioDispatcher) {
            try {
                val warrantyId = inputData.getString(KEY_WARRANTY_ID)
                val uploadedImageUrl = inputData.getString(KEY_UPLOADED_IMAGE_URL)

                if (warrantyId.isNullOrBlank() || uploadedImageUrl.isNullOrBlank()) {
                    Log.e(TAG, "Missing required input data: warrantyId or uploadedImageUrl")
                    return@withContext WorkResult.failure()
                }

                return@withContext when (val result =
                    warrantiesRepository.updateRemoteUrlPhotoLocaly(warrantyId, uploadedImageUrl)) {
                    is CommonResult.Success -> {
                        Log.i(TAG, "Successfully completed photo upload for warranty $warrantyId")
                        WorkResult.success()
                    }
                    is CommonResult.Error -> {
                        Log.e(TAG, "Failed to update warranty photo URL: ${result.throwable.message}")
                        WorkResult.failure()
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to complete photo upload", e)
                WorkResult.failure()
            }
        }
    }

    companion object {
        private const val TAG = "CompletePhotoUploadWorker"
    }
}
