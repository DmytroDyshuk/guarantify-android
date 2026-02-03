package com.guarantify.util.date.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import java.time.format.DateTimeFormatter
import java.util.Locale

@Module
@InstallIn(SingletonComponent::class)
object DateFormattersModule {

    @Provides
    @Singleton
    fun provideAppDateFormatProvider(): AppDateFormatProvider {
        return object : AppDateFormatProvider {
            override val fullText = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)
            override val shortText = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
            override val numeric = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH)
        }
    }

}