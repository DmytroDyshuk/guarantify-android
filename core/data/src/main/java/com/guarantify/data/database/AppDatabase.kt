package com.guarantify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.guarantify.data.database.converters.DateConverter
import com.guarantify.data.database.converters.SyncStatusConverter
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.database.entity.WarrantyEntity

@Database(entities = [WarrantyEntity::class], version = 1)
@TypeConverters(DateConverter::class, SyncStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun warrantyDao(): WarrantyDao
}