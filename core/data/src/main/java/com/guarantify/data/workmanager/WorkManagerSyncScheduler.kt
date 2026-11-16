package com.guarantify.data.workmanager

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.guarantify.domain.sync.SyncScheduler
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit

class WorkManagerSyncScheduler @Inject constructor(
    private val workManager: WorkManager
) : SyncScheduler {

    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    override fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = PERIODIC_SYNC_INTERVAL_HOURS,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = PERIODIC_SYNC_FLEX_HOURS,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setConstraints(networkConstraints)
            .addTag(WarrantyWork.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WarrantyWork.PERIODIC_SYNC_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun requestImmediateSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints)
            .addTag(WarrantyWork.TAG)
            .build()

        workManager.enqueueUniqueWork(
            WarrantyWork.IMMEDIATE_SYNC_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    override suspend fun cancelAllWork() {
        workManager.cancelAllWorkByTag(WarrantyWork.TAG).await()
        // sync work enqueued by older app versions has no tag
        workManager.cancelUniqueWork(WarrantyWork.PERIODIC_SYNC_NAME).await()
        workManager.cancelUniqueWork(WarrantyWork.IMMEDIATE_SYNC_NAME).await()
    }

    private companion object {
        const val PERIODIC_SYNC_INTERVAL_HOURS = 5L
        const val PERIODIC_SYNC_FLEX_HOURS = 1L
    }

}
