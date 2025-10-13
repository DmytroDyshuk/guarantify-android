package com.guarantify.data.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSource
import com.guarantify.data.network.firebase.FirebaseWarrantyDataSourceImpl
import com.guarantify.data.repository.GoogleAuthRepositoryImpl
import com.guarantify.domain.repository.GoogleAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideAuthFirebase(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideGoogleAuthRepository(
        firebaseAuth: FirebaseAuth
    ): GoogleAuthRepository = GoogleAuthRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideFirebaseWarrantyDataSource(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): FirebaseWarrantyDataSource = FirebaseWarrantyDataSourceImpl(firebaseFirestore, firebaseAuth)

}