package com.yeyint.composebase.di

import com.yeyint.composebase.data.datasource.AppVersionRemoteDataSource
import com.yeyint.composebase.network.components.TokenManager
import com.yeyint.composebase.network.datasource.FakeAppVersionRemoteDataSource
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

    // Fake until the backend has a version endpoint — swap this one binding for the real
    // implementation; nothing above the data source changes.
    @Singleton
    @Binds
    abstract fun bindAppVersionRemoteDataSource(
        fake: FakeAppVersionRemoteDataSource
    ): AppVersionRemoteDataSource

//    @Singleton
//    @Binds
//    abstract fun userDataSource(
//        userDataSourceImpl: UserDataSourceImpl
//    ): UserDataSource

}