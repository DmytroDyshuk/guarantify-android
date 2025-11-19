package com.guarantify.ui.di

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
            override val longDate = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)
            override val shortDate = DateTimeFormatter.ofPattern("dd MM yyyy", Locale.ENGLISH)
        }
    }

}