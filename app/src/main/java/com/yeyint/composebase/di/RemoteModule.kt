package com.yeyint.composebase.di

import com.yeyint.composebase.network.components.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteModule {
    @Singleton
    @Binds
    @Named("token_manager")
    abstract fun bindTokenManager(tokenManager: TokenManager): TokenManager

//    @Singleton
//    @Binds
//    abstract fun userDataSource(
//        userDataSourceImpl: UserDataSourceImpl
//    ): UserDataSource

}