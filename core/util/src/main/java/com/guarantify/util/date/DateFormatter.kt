package com.guarantify.util.date

import com.guarantify.util.date.di.AppDateFormatProvider
import com.guarantify.util.extensions.toLocalDate
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Singleton
class DateFormatter @Inject constructor(
    private val formatProvider: AppDateFormatProvider
) {
    fun formatToShortText(millis: Long): String {
        return millis.toLocalDate().format(formatProvider.shortDate)
    }

    fun formatToLongText(millis: Long): String {
        return millis.toLocalDate().format(formatProvider.longDate)
    }

    fun formatToShortText(date: LocalDate): String {
        return date.format(formatProvider.shortDate)
    }

    fun formatToLongText(date: LocalDate): String {
        return date.format(formatProvider.longDate)
    }

}