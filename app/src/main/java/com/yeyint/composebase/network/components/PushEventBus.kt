package com.yeyint.composebase.network.components

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Emitted by the push service for every push it shows, carrying the push's `type`, so an
 * already-open screen can react without the user pulling to refresh: a bell badge refetching its
 * count, a list topping itself up. Same "the service can't touch UI state, so hand off through a
 * bus" reasoning as [AuthEventBus]. Collectors that only care about some pushes filter on the type.
 */
@Singleton
class PushEventBus @Inject constructor() {
    private val _pushes = MutableSharedFlow<String?>(extraBufferCapacity = 4)
    val pushes: SharedFlow<String?> = _pushes.asSharedFlow()

    fun emit(type: String?) {
        _pushes.tryEmit(type)
    }
}
