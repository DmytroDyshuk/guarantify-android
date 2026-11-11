package com.guarantify.domain.preferences

import kotlinx.coroutines.flow.Flow

interface SyncPreferencesManager {
    suspend fun getLastSyncTimestamp(): Long
    suspend fun updateLastSyncTimestamp(timestamp: Long)
    fun observeFirstSyncAttempted(): Flow<Boolean>
    suspend fun markFirstSyncAttempted()
}
