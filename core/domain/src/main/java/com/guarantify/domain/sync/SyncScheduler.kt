package com.guarantify.domain.sync

interface SyncScheduler {
    fun schedulePeriodicSync()

    fun requestImmediateSync()

    suspend fun cancelAllWork()
}
