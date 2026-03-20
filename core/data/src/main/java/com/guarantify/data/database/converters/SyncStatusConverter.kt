package com.guarantify.data.database.converters

import androidx.room.TypeConverter
import com.guarantify.domain.model.sync.SyncStatus

object SyncStatusConverter {

    @TypeConverter
    @JvmStatic
    fun fromSyncStatusToString(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToSyncStatus(value: String): SyncStatus {
        return try {
            SyncStatus.valueOf(value)
        } catch (_: IllegalArgumentException) {
            SyncStatus.PENDING
        }
    }

}