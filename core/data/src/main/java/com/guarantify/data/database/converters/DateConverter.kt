package com.guarantify.data.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun fromLocalDateToEpochDay(date: LocalDate): Long {
        return date.toEpochDay()
    }

    @TypeConverter
    @JvmStatic
    fun fromEpochDayToLocalDate(epochDay: Long): LocalDate {
        return LocalDate.ofEpochDay(epochDay)
    }
}