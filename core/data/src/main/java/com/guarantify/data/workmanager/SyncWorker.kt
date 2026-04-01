package com.guarantify.data.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.guarantify.domain.model.sync.SyncError
import com.guarantify.domain.repository.SyncWarrantiesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.work.ListenableWorker.Result as WorkResult
import com.guarantify.common.result.Result as CommonResult

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncWarrantiesRepository: SyncWarrantiesRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): WorkResult {
        return when (val result = syncWarrantiesRepository.syncWarranties()) {
            is CommonResult.Success -> WorkResult.success()
            is CommonResult.Error -> {
                when (result.throwable) {
                    is SyncError.DatabaseError -> WorkResult.failure()
                    is SyncError.NetworkError -> WorkResult.retry()
                    is SyncError.AuthError -> WorkResult.failure()
                    is SyncError.UnknownError -> WorkResult.retry()
                    else -> WorkResult.failure()
                }
            }
        }
    }

}