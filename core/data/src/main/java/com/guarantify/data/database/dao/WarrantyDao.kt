package com.guarantify.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.mapper.toEntityWithGeneratedIdIfNeeded
import com.guarantify.domain.model.sync.SyncStatus
import com.guarantify.domain.model.Warranty
import kotlinx.coroutines.flow.Flow

@Dao
interface WarrantyDao {
    @Query("SELECT * FROM warranties WHERE isDeleted = 0")
    fun getAllWarranties(): Flow<List<WarrantyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdateWarranty(warranty: WarrantyEntity)

    @Transaction
    suspend fun upsertWithPhotoLogic(warranty: Warranty): WarrantyEntity {
        val oldWarranty = getWarrantyById(warranty.id)

        val photoUri = warranty.localPhotoUri?.takeUnless { it.isBlank() }
        val isPhotoChanged = oldWarranty?.localPhotoUri != photoUri

        val syncStatus = if (photoUri != null && isPhotoChanged) {
            SyncStatus.PENDING
        } else {
            SyncStatus.READY_TO_SYNC
        }

        val entity = warranty.toEntityWithGeneratedIdIfNeeded().copy(
            syncStatus = syncStatus,
            remotePhotoUrl = if (isPhotoChanged) null else oldWarranty?.remotePhotoUrl
        )

        createOrUpdateWarranty(entity)

        return entity
    }

    @Query("SELECT * FROM warranties WHERE id = :id AND isDeleted = 0")
    suspend fun getWarrantyById(id: String): WarrantyEntity?

    @Query("SELECT * FROM warranties WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedWarranties(): List<WarrantyEntity>

    @Query("UPDATE warranties SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateWarrantySyncStatus(id: String, syncStatus: SyncStatus)

    @Query("UPDATE warranties SET syncStatus = :status WHERE id IN (:ids)")
    suspend fun updateSyncStatusForIds(ids: List<String>, status: SyncStatus)

    @Query("UPDATE warranties SET isDeleted = 1, updatedAt = :updatedAt, syncStatus = :syncStatus WHERE id = :id")
    suspend fun softDeleteWarranty(
        id: String,
        updatedAt: Long,
        syncStatus: SyncStatus = SyncStatus.READY_TO_SYNC
    )

    @Delete
    suspend fun hardDeleteWarranties(warranties: List<WarrantyEntity>)

    @Query("SELECT MAX(updatedAt) FROM warranties")
    suspend fun getLastUpdatedTimestamp(): Long?
}