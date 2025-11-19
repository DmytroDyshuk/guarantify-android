package com.guarantify.ui.di

import java.time.format.DateTimeFormatter

interface AppDateFormatProvider {
    val longDate: DateTimeFormatter
    val shortDate: DateTimeFormatter
}