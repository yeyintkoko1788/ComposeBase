package com.yeyint.composebase.network.components

import com.yeyint.composebase.domain.model.MaintenanceInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Emits a one-shot event whenever any API call comes back the maintenance status (see [com.yeyint.composebase.network.exception.NetworkExceptionInterceptor]),
 * regardless of which screen made the call — same "interceptor can't touch UI state directly, so
 * hand off through a bus" reasoning as [AuthEventBus]. The top-level Activity observes this and
 * takes over the whole app with a maintenance screen, not just the one screen that happened to be
 * active when the backend went down.
 */
@Singleton
class MaintenanceEventBus @Inject constructor() {
    // The payload is whatever the maintenance response itself said — empty when it had nothing
    // parseable (e.g. a bare proxy 503 page), in which case the screen uses its built-in text.
    private val _events = MutableSharedFlow<MaintenanceInfo>(extraBufferCapacity = 1)
    val events: SharedFlow<MaintenanceInfo> = _events.asSharedFlow()

    fun emitMaintenanceDetected(info: MaintenanceInfo = MaintenanceInfo()) {
        _events.tryEmit(info)
    }
}
