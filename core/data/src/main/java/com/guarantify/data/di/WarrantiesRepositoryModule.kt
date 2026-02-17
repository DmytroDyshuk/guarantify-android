package com.guarantify.data.di

import com.guarantify.data.repository.WarrantiesRepositoryImpl
import com.guarantify.domain.repository.WarrantiesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface WarrantiesRepositoryModule {

    @Binds
    @Singleton
    fun bindWarrantiesRepository(impl: WarrantiesRepositoryImpl): WarrantiesRepository

}