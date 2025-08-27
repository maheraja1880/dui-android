package com.sample.dynamicui.di

import com.sample.dynamicui.data.repository.DynamicRepositoryImpl
import com.sample.dynamicui.domain.repository.DynamicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRepository(): DynamicRepository = DynamicRepositoryImpl(
        baseUrl = "https://api.example.com" // TODO: configure from BuildConfig
    )
}