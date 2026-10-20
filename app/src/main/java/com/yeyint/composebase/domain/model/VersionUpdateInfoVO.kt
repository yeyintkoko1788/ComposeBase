package com.yeyint.composebase.domain.model

/**
 * The app's own update check — driven by our backend, not the Play Store's In-App Update
 * API, since side-loaded/staging builds need a version source that doesn't depend on a Play
 * listing existing. The backend has no download-link field of its own; "Update Now" opens this
 * app's own Play Store listing directly (see `openPlayStoreListing`).
 */
data class VersionUpdateInfoVO(
    val latestVersion: String,
    val releaseNotes: String,
    val forceUpdate: Boolean = false
)
