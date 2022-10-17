package com.rago.cameraxjetpackcompose.di

import android.content.Context
import com.rago.cameraxjetpackcompose.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUtils(
        @ApplicationContext context: Context
    ): Utils = Utils(context)
}