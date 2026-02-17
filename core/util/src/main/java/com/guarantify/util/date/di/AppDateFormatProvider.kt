package com.guarantify.util.date.di

import java.time.format.DateTimeFormatter

interface AppDateFormatProvider {
    val fullText: DateTimeFormatter
    val shortText: DateTimeFormatter
    val numeric: DateTimeFormatter
}