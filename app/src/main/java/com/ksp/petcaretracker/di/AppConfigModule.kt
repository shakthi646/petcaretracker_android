package com.ksp.petcaretracker.di

import android.content.Context
import com.ksp.corelibrary.coreInterface.DefaultAppConfig
import com.ksp.petcaretracker.provider.DefaultAppConfigProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    fun provideDefaultAppConfig(@ApplicationContext context: Context): DefaultAppConfig {
        return DefaultAppConfigProvider(context)
    }
}
