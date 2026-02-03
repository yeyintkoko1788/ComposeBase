package com.yeyint.composebase.di

import com.yeyint.composebase.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ConstantModule {
    init {
        val libPath = System.getProperty("java.library.path")
        println("java.library.path=$libPath")

        val libraryName = "native-lib"
        println("Trying to load '$libraryName'")

        System.loadLibrary(libraryName)
    }

    private external fun getBaseURL(): String?

    private external fun getBaseStagingURL(): String?

    @Provides
    @Singleton
    @Named("base_url")
    fun providesBaseUrl(): String {
        return if (BuildConfig.IS_PRODUCTION) {
            getBaseURL()!!
        } else {
            getBaseStagingURL()!!
        }
    }
}