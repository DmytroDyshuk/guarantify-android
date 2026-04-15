package com.guarantify.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.guarantify.data.preferences.SyncPreferencesManagerImpl
import com.guarantify.domain.preferences.SyncPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sync_prefs"
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.syncDataStore
    }

    @Provides
    @Singleton
    fun provideSyncPreferencesManager(dataStore: DataStore<Preferences>): SyncPreferencesManager {
        return SyncPreferencesManagerImpl(dataStore)
    }

}