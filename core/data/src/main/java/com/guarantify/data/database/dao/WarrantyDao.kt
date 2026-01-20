package com.guarantify.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guarantify.data.database.entity.WarrantyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarrantyDao {
    @Query("SELECT * FROM warranties")
    fun getAllWarranties(): Flow<List<WarrantyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdateWarranty(warranty: WarrantyEntity)

    @Delete
    suspend fun deleteWarranty(warranty: WarrantyEntity)

    @Query("SELECT * FROM warranties WHERE isSynced = 0")
    suspend fun getUnsyncedWarranties(): List<WarrantyEntity>
}