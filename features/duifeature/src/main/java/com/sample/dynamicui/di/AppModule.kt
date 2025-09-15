package com.sample.dynamicui.di

import com.sample.dynamicui.data.repository.DataRepositoryImpl
import com.sample.dynamicui.data.repository.LayoutRepositoryImpl
import com.sample.dynamicui.domain.repository.DataRepository
import com.sample.dynamicui.domain.repository.LayoutRepository
import com.sample.dynamicui.domain.usecase.GetData
import com.sample.dynamicui.domain.usecase.GetLayout
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
    fun provideRepository(): LayoutRepository = LayoutRepositoryImpl(
        baseUrl = "https://api.example.com" // TODO: configure from BuildConfig
    )

    @Provides
    @Singleton
    fun provideDataRepository(): DataRepository = DataRepositoryImpl()

    @Provides
    @Singleton
    fun provideGetLayout(repository: LayoutRepository): GetLayout = GetLayout(repository)

    @Provides
    @Singleton
    fun provideGetData(repository: DataRepository): GetData = GetData(repository)

}