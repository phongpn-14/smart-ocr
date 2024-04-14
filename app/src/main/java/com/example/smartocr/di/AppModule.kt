package com.example.smartocr.di

import com.example.smartocr.data.DataRepository
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.util.Network
import com.example.smartocr.util.NetworkConnectivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun provideNetworkConnectivity(netWork: Network): NetworkConnectivity

    @Binds
    @Singleton
    abstract fun provideDataRepository(dataRepository: DataRepository): DataRepositorySource
}