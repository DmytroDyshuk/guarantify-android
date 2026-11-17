package com.guarantify.data.di

import com.guarantify.data.photo.PhotoStorageImpl
import com.guarantify.domain.photo.PhotoStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PhotoStorageModule {

    @Binds
    @Singleton
    fun bindPhotoStorage(impl: PhotoStorageImpl): PhotoStorage

}