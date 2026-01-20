package com.guarantify.data.di

import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSource
import com.guarantify.data.repository.WarrantiesRepositoryImpl
import com.guarantify.domain.repository.WarrantiesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
interface WarrantiesRepositoryModule {

    @Binds
    @Singleton
    fun bindWarrantiesRepository(impl: WarrantiesRepositoryImpl): WarrantiesRepository

}