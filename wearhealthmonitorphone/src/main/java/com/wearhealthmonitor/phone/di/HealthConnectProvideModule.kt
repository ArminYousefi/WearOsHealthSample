package com.wearhealthmonitor.phone.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.wearhealthmonitor.phone.data.SleepRepositoryImpl
import com.wearhealthmonitor.phone.domain.HealthConnectPermissionManager
import com.wearhealthmonitor.phone.domain.repository.SleepRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthConnectProvideModule {

    @Provides
    @Singleton
    fun provideHealthConnectClient(
        @ApplicationContext context: Context
    ): HealthConnectClient = HealthConnectClient.getOrCreate(context)

    @Provides
    @Singleton
    fun providePermissionManager(
        client: HealthConnectClient
    ): HealthConnectPermissionManager = HealthConnectPermissionManager(client)

    @Provides
    @Singleton
    fun provideSleepRepository(
        client: HealthConnectClient
    ): SleepRepository = SleepRepositoryImpl(client)
}