package com.yeyint.composebase.network.datasource

import com.yeyint.composebase.data.datasource.AppVersionRemoteDataSource
import com.yeyint.composebase.domain.model.VersionUpdateInfoVO
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stands in until the backend has a version endpoint: always "no update". Bound in
 * `RemoteModule` — writing the real `AppVersionRemoteDataSourceImpl` and changing that one
 * `@Binds` is the whole job; the repository, [com.yeyint.composebase.ui.viewmodel.MainViewModel]
 * and the update sheet are already finished. The real implementation compares
 * `AppVersion.parse(BuildConfig.VERSION_NAME)` against the server's latest and returns a
 * [VersionUpdateInfoVO] only when the server's is newer.
 */
@Singleton
class FakeAppVersionRemoteDataSource @Inject constructor() : AppVersionRemoteDataSource {
    override suspend fun checkForUpdate(): Result<VersionUpdateInfoVO?> = Result.success(null)
}
