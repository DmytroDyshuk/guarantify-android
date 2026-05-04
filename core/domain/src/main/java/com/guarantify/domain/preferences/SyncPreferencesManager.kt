package com.guarantify.domain.preferences

interface SyncPreferencesManager {
    suspend fun getLastSyncTimestamp(): Long
    suspend fun updateLastSyncTimestamp(timestamp: Long)
}