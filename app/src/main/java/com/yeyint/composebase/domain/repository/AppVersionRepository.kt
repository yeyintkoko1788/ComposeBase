package com.yeyint.composebase.domain.repository

import com.yeyint.composebase.domain.model.VersionUpdateInfoVO

interface AppVersionRepository {

    /** `null` means no update to show. */
    suspend fun checkForUpdate(): Result<VersionUpdateInfoVO?>
}
