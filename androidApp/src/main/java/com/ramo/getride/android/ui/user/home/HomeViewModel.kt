package com.ramo.getride.android.ui.user.home

import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.data.supaBase.signOutAuth
import com.ramo.getride.data.map.Location
import com.ramo.getride.data.util.REALM_SUCCESS
import com.ramo.getride.data.map.fetchAndDecodeRoute
import com.ramo.getride.di.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

class HomeViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    fun fetchRoute(start: GoogleLatLng, end: GoogleLatLng) {
        setIsProcess(true)
        launchBack {
            fetchAndDecodeRoute(
                Location(lat = start.latitude, lng = start.longitude),
                Location(lat = end.latitude, lng = end.longitude)
            )?.also { routePoints ->
                PolylineOptions()
                    .addAll(PolyUtil.decode(routePoints))
                    .points.also { points ->
                        _uiState.update { state ->
                            state.copy(routePoints = points, isProcess = false)
                        }
                    }
            } ?: setIsProcess(false)
        }
    }

    private fun List<Location>.toGoogleLatLng(): List<GoogleLatLng> {
        return map {
            GoogleLatLng(it.lat, it.lng)
        }
    }

    fun signOut(invoke: () -> Unit, failed: () -> Unit) {
        setIsProcess(true)
        launchBack {
            project.pref.deletePrefAll().checkDeleting({
                signOutAuth({
                    setIsProcess(false)
                    invoke()
                }, {
                    setIsProcess(false)
                    failed()
                })
            }, {
                setIsProcess(false)
                failed()
            })
        }
    }


    private suspend fun Int.checkDeleting(invoke: suspend () -> Unit, failed: suspend () -> Unit) {
        if (this@checkDeleting == REALM_SUCCESS) {
            invoke.invoke()
        } else {
            failed.invoke()
        }
    }

    data class State(
        val routePoints: List<GoogleLatLng> = emptyList(),
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val carModule: String = "",
        val carNumber: String = "",
        val carColor: String = "",
        val password: String = "",
        val isLoginScreen: Boolean = false,
        val isProcess: Boolean = false,
        val isErrorPressed: Boolean = false,
    )
}