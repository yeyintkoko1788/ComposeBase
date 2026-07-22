package com.yeyint.composebase.ui.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tracks internet connectivity as a [StateFlow], counting a network as "connected" only once
 * it reports NET_CAPABILITY_VALIDATED (i.e. it can actually reach the internet, not just that a
 * radio is up). Multiple networks are tracked independently so losing one doesn't flip the
 * flow to false while another validated network is still active.
 */
class NetworkStatusHelper(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(isCurrentlyConnected())
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // Track each network that has been validated independently
    private val validatedNetworks = mutableSetOf<Network>()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // Don't set connected yet — wait for VALIDATED in onCapabilitiesChanged
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                validatedNetworks.add(network)
            } else {
                validatedNetworks.remove(network)
            }
            _isConnected.value = validatedNetworks.isNotEmpty()
        }

        override fun onLost(network: Network) {
            validatedNetworks.remove(network)
            _isConnected.value = validatedNetworks.isNotEmpty()
        }

        override fun onUnavailable() {
            validatedNetworks.clear()
            _isConnected.value = false
        }
    }

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    init {
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun isCurrentlyConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities != null
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}