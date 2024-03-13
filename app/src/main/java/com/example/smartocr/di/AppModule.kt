package com.example.smartocr.di

import com.example.smartocr.util.Network
import com.example.smartocr.util.NetworkConnectivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun provideNetworkConnectivity(netWork: Network): NetworkConnectivity
}