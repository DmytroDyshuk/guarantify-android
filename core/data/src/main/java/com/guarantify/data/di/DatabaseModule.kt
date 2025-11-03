package com.guarantify.data.di

import android.content.Context
import androidx.room.Room
import com.guarantify.data.database.AppDatabase
import com.guarantify.data.database.dao.WarrantyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWarrantyDao(database: AppDatabase): WarrantyDao {
        return database.warrantyDao()
    }

}