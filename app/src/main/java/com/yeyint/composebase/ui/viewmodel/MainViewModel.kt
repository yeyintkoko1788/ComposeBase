package com.yeyint.composebase.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.yeyint.composebase.domain.exception.GenericErrorMessageFactory
import com.yeyint.composebase.domain.model.MaintenanceInfo
import com.yeyint.composebase.domain.model.VersionUpdateInfoVO
import com.yeyint.composebase.domain.repository.AppVersionRepository
import com.yeyint.composebase.network.components.AuthEventBus
import com.yeyint.composebase.network.components.MaintenanceEventBus
import com.yeyint.composebase.network.components.MyPreference
import com.yeyint.composebase.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State that belongs to the whole app rather than to any one screen: backend maintenance, a
 * session that ended on its own, and a newer app version. Owned by the Activity's `MainScreen`,
 * which reacts to each by taking over the screen or showing a sheet.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    flowGenericErrorMessageFactory: GenericErrorMessageFactory,
    myPreference: MyPreference,
    private val appVersionRepository: AppVersionRepository,
    maintenanceEventBus: MaintenanceEventBus,
    authEventBus: AuthEventBus
) : BaseViewModel(flowGenericErrorMessageFactory, myPreference) {

    private val _versionUpdateInfo = MutableStateFlow<VersionUpdateInfoVO?>(null)
    val versionUpdateInfo: StateFlow<VersionUpdateInfoVO?> = _versionUpdateInfo.asStateFlow()

    /**
     * Sticky, not a plain flow off the bus — once the backend says it's under maintenance there's
     * no "it recovered" signal to watch for (unlike connectivity), so this only ever becomes
     * non-null and stays that way for the rest of the process. A fresh app open starts a new
     * process, which is the only way out.
     */
    val maintenance: StateFlow<MaintenanceInfo?> = maintenanceEventBus.events
        // A later maintenance response missing a part doesn't wipe out text an earlier one supplied.
        .scan<MaintenanceInfo, MaintenanceInfo?>(null) { previous, info ->
            MaintenanceInfo(
                title = info.title ?: previous?.title,
                description = info.description ?: previous?.description
            )
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    private val _sessionExpired = MutableStateFlow(false)

    /**
     * True from the moment a request ends the session (a 401 the token refresh couldn't recover
     * from) until the user acknowledges the prompt. Several requests failing at once each emit an
     * event; setting an already-true flag again is a no-op, which keeps it to one prompt.
     */
    val sessionExpired: StateFlow<Boolean> = _sessionExpired.asStateFlow()

    init {
        viewModelScope.launch { authEventBus.events.collect { _sessionExpired.value = true } }
        checkForUpdate()
    }

    /** The sheet's only action; the caller then navigates to Login. */
    fun acknowledgeSessionExpired() {
        _sessionExpired.value = false
    }

    private fun checkForUpdate() {
        viewModelScope.launch {
            // A failed check just means no prompt this launch — nobody is waiting on it.
            _versionUpdateInfo.value = appVersionRepository.checkForUpdate().getOrNull()
        }
    }
}
