package com.guarantify.data.di

import com.guarantify.data.network.firebase.firestore.FirebaseWarrantyDataSource
import com.guarantify.data.network.firebase.firestore.FirebaseWarrantyDataSourceImpl
import com.guarantify.data.repository.GoogleAuthRepositoryImpl
import com.guarantify.domain.repository.GoogleAuthRepository
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
    fun bindGoogleAuthRepository(
        impl: GoogleAuthRepositoryImpl
    ): GoogleAuthRepository

    @Binds
    @Singleton
    fun bindFirebaseWarrantyDataSource(
        impl: FirebaseWarrantyDataSourceImpl
    ): FirebaseWarrantyDataSource

}