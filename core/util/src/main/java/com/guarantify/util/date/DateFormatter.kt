package com.guarantify.util.date

import com.guarantify.util.date.di.AppDateFormatProvider
import com.guarantify.util.extensions.toLocalDate
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class DateFormatter @Inject constructor(
    private val formatProvider: AppDateFormatProvider
) {
    fun formatToShortText(millis: Long): String {
        return millis.toLocalDate().format(formatProvider.shortText)
    }

    fun formatToFullText(millis: Long): String {
        return millis.toLocalDate().format(formatProvider.fullText)
    }

    fun formatToNumericText(millis: Long): String {
        return millis.toLocalDate().format(formatProvider.numeric)
    }

    fun formatToShortText(date: LocalDate): String {
        return date.format(formatProvider.shortText)
    }

    fun formatToFullText(date: LocalDate): String {
        return date.format(formatProvider.fullText)
    }

    fun formatToNumericText(date: LocalDate): String {
        return date.format(formatProvider.numeric)
    }

}