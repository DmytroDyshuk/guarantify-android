package com.guarantify.util.date.di

import java.time.format.DateTimeFormatter

interface AppDateFormatProvider {
    val longDate: DateTimeFormatter
    val shortDate: DateTimeFormatter
}