package com.yeyint.composebase.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {

//    @Singleton
//    @Binds
//    abstract fun bindUserDataCacheSource(userDataCacheSourceImpl: UserDataCacheSourceImpl) : UserDataCacheSource
}