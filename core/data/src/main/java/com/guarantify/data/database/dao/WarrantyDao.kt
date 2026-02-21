package com.guarantify.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface WarrantyDao {
    @Query("SELECT * FROM warranties WHERE isDeleted = 0")
    fun getAllWarranties(): Flow<List<WarrantyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdateWarranty(warranty: WarrantyEntity)

    @Query("SELECT * FROM warranties WHERE id = :id AND isDeleted = 0")
    suspend fun getWarrantyById(id: String): WarrantyEntity?

    @Query("SELECT * FROM warranties WHERE syncStatus != 'COMPLETED'")
    suspend fun getUnsyncedWarranties(): List<WarrantyEntity>

    @Query("UPDATE warranties SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateSyncStatus(id: String, syncStatus: SyncStatus)

    @Query("UPDATE warranties SET isDeleted = 1, updatedAt = :updatedAt, syncStatus = :syncStatus WHERE id = :id")
    suspend fun markAsDeleted(
        id: String,
        updatedAt: Long,
        syncStatus: SyncStatus = SyncStatus.PENDING
    )

    @Delete
    suspend fun hardDeleteWarranty(warranty: WarrantyEntity)
}