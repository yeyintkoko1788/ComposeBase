package com.yeyint.composebase.network.components

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Emits a one-shot event whenever a token refresh attempt fails, so the UI layer (e.g. the
 * top-level Activity/NavGraph) can react by routing the user back to login. Kept separate from
 * [AuthenticationInterceptor] itself since interceptors run on background threads and shouldn't
 * touch UI state directly.
 */
@Singleton
class AuthEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun emitSessionExpired() {
        _events.tryEmit(Unit)
    }
}
