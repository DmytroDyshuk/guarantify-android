package com.guarantify.data.di

import com.guarantify.data.workmanager.WorkManagerSyncScheduler
import com.guarantify.domain.sync.SyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SyncModule {

    @Binds
    @Singleton
    fun bindSyncScheduler(impl: WorkManagerSyncScheduler): SyncScheduler

}
