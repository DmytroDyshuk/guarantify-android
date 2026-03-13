package com.guarantify.data.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.guarantify.domain.repository.SyncWarrantiesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncWarrantiesRepository: SyncWarrantiesRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

}