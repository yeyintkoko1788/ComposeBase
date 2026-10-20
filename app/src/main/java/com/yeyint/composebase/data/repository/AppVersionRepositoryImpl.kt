package com.yeyint.composebase.data.repository

import com.yeyint.composebase.data.datasource.AppVersionRemoteDataSource
import com.yeyint.composebase.domain.model.VersionUpdateInfoVO
import com.yeyint.composebase.domain.repository.AppVersionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVersionRepositoryImpl @Inject constructor(
    private val remote: AppVersionRemoteDataSource
) : AppVersionRepository {

    override suspend fun checkForUpdate(): Result<VersionUpdateInfoVO?> = remote.checkForUpdate()
}
