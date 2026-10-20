package com.yeyint.composebase.data.datasource

import com.yeyint.composebase.domain.model.VersionUpdateInfoVO

interface AppVersionRemoteDataSource {

    /** `null` means no update to show — either the rider's already current, or the backend's
     *  `version` string couldn't be parsed as a comparable version (a non-numeric staging tag,
     *  say) and there's nothing safe to compare against. */
    suspend fun checkForUpdate(): Result<VersionUpdateInfoVO?>
}
