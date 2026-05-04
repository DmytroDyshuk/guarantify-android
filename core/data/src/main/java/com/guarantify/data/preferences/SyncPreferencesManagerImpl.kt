package com.guarantify.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.guarantify.domain.preferences.SyncPreferencesManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SyncPreferencesManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SyncPreferencesManager {

    private val lastSyncKey = longPreferencesKey("last_sync_timestamp")

    override suspend fun getLastSyncTimestamp(): Long {
        return dataStore.data.map { preferences ->
            preferences[lastSyncKey] ?: 0L
        }.first()
    }

    override suspend fun updateLastSyncTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[lastSyncKey] = timestamp
        }
    }

    override suspend fun isFirstSyncCompleted(): Boolean {
        return getLastSyncTimestamp() > 0L
    }

}