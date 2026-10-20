package com.yeyint.composebase.di

import com.yeyint.composebase.data.repository.AppVersionRepositoryImpl
import com.yeyint.composebase.domain.repository.AppVersionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAppVersionRepository(impl: AppVersionRepositoryImpl): AppVersionRepository

//    @Singleton
//    @Binds
//    abstract fun bindAuthenticationRepository(
//        loginRepositoryImpl : LoginRepositoryImpl
//    ) : LoginRepository

}