package com.guarantify.data.di

import com.guarantify.data.network.firebase.firestore.FirestoreWarrantyDataSource
import com.guarantify.data.network.firebase.firestore.FirestoreWarrantyDataSourceImpl
import com.guarantify.data.network.firebase.storage.WarrantyPhotoStorage
import com.guarantify.data.network.firebase.storage.WarrantyPhotoStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FirebaseModule {

    @Binds
    @Singleton
    fun bindFirestoreWarrantyDataSource(
        impl: FirestoreWarrantyDataSourceImpl
    ): FirestoreWarrantyDataSource

    @Binds
    @Singleton
    fun bindWarrantyPhotoStorage(
        impl: WarrantyPhotoStorageImpl
    ): WarrantyPhotoStorage

}