package com.guarantify.data.di

import com.guarantify.data.repository.AuthRepositoryImpl
import com.guarantify.data.repository.SyncWarrantiesRepositoryImpl
import com.guarantify.data.repository.WarrantiesRepositoryImpl
import com.guarantify.domain.repository.AuthRepository
import com.guarantify.domain.repository.SyncWarrantiesRepository
import com.guarantify.domain.repository.WarrantiesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindWarrantiesRepository(impl: WarrantiesRepositoryImpl): WarrantiesRepository

    @Binds
    @Singleton
    fun bindSyncWarrantiesRepository(impl: SyncWarrantiesRepositoryImpl): SyncWarrantiesRepository

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

}