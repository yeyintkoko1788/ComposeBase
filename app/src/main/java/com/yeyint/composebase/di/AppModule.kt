package com.yeyint.composebase.di

import android.app.Application
import android.content.Context
import com.yeyint.composebase.domain.exception.GenericErrorMessageFactory
import com.yeyint.composebase.network.exception.NetworkExceptionMessageFactory
import com.yeyint.composebase.network.provider.DefaultDispatcherProvider
import com.yeyint.composebase.network.provider.DispatcherProvider
import com.yeyint.composebase.ui.exception.GenericErrorMessageFactoryImpl
import com.yeyint.composebase.ui.exception.NetworkExceptionMessageFactoryImpl
import com.yeyint.composebase.ui.helper.NetworkStatusHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Singleton
//    @Provides
//    fun provideDatabase(
//        @ApplicationContext context: Context
//    ) = Room
//        .databaseBuilder(context, AlphaUserDatabase::class.java, context.getString(R.string.db_name))
//        .fallbackToDestructiveMigration()
//        .build()
//
//    @Provides
//    fun provideUserDao(database: AlphaUserDatabase): UserDao {
//        return database.userDao()
//    }

    @Provides
    @Singleton
    fun flowGenericErrorMessageFactory(flowGenericErrorMessageFactory: GenericErrorMessageFactoryImpl): GenericErrorMessageFactory =
        flowGenericErrorMessageFactory

    @Provides
    @Singleton
    fun providerDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

    @Provides
    @Singleton
    fun providesContext(application: Application): Context = application.applicationContext!!

    @Provides
    @Singleton
    fun networkErrorMessageFactory(networkExceptionMessageFactory: NetworkExceptionMessageFactoryImpl): NetworkExceptionMessageFactory =
        networkExceptionMessageFactory

    @Provides
    @Singleton
    fun provideNetworkStatusHelper(@ApplicationContext context: Context): NetworkStatusHelper {
        return NetworkStatusHelper(context)
    }
}