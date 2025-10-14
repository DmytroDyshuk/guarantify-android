package com.guarantify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guarantify.data.database.dao.WarrantyDao
import com.guarantify.data.database.entity.WarrantyEntity

@Database(entities = [WarrantyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun warrantyDao(): WarrantyDao
}