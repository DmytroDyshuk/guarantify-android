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
    @Query("SELECT * FROM warranties")
    fun getAllWarranties(): Flow<List<WarrantyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdateWarranty(warranty: WarrantyEntity)

    @Query("SELECT * FROM warranties WHERE id = :id")
    suspend fun getWarrantyById(id: String): WarrantyEntity?

    @Delete
    suspend fun deleteWarranty(warranty: WarrantyEntity)

    @Query("SELECT * FROM warranties WHERE syncStatus != 'COMPLETED'")
    suspend fun getUnsyncedWarranties(): List<WarrantyEntity>

    @Query("UPDATE warranties SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateSyncStatus(id: String, syncStatus: SyncStatus)
}