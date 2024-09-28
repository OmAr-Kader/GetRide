package com.ramo.getride.android.ui.user.home

import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.data.supaBase.signOutAuth
import com.ramo.getride.data.map.Location
import com.ramo.getride.data.util.REALM_SUCCESS
import com.ramo.getride.data.map.fetchAndDecodeRoute
import com.ramo.getride.data.map.fetchPlaceName
import com.ramo.getride.data.model.PreferenceData
import com.ramo.getride.data.util.calculateFare
import com.ramo.getride.data.util.convertMetersToKmAndMeters
import com.ramo.getride.data.util.convertSecondsToHoursMinutes
import com.ramo.getride.di.Project
import com.ramo.getride.global.base.PREF_LAST_LATITUDE
import com.ramo.getride.global.base.PREF_LAST_LONGITUDE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

class HomeViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun fetchRoute(start: GoogleLatLng, end: GoogleLatLng) {
        setIsProcess(true)
        launchBack {
            fetchAndDecodeRoute(
                Location(lat = start.latitude, lng = start.longitude),
                Location(lat = end.latitude, lng = end.longitude)
            ) { routePoints, duration, distance ->
                routePoints?.also { routes ->
                    PolylineOptions()
                        .addAll(PolyUtil.decode(routes))
                        .points.also { points ->
                            (duration?.convertSecondsToHoursMinutes() ?: "").also { durationText ->
                                (distance?.convertMetersToKmAndMeters() ?: "").also { distanceText ->
                                    (if (distanceText.isEmpty()) "" else durationText + " (${distanceText})").also { durationDistance ->
                                        _uiState.update { state ->
                                            state.copy(
                                                mapData = state.mapData.copy(
                                                    routePoints = points,
                                                    duration = duration,
                                                    durationText = durationText,
                                                    distance = distance,
                                                    distanceText = distanceText,
                                                    durationDistance = durationDistance,
                                                    fare = calculateFare(duration = duration, distance = distance)
                                                ),
                                                isProcess = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                } ?: setIsProcess(false)
            }
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

    fun setMapMarks(startPoint: GoogleLatLng? = null, endPoint: GoogleLatLng? = null, currentLocation: GoogleLatLng? = null) {
        startPoint?.also {
            launchBack {
                fetchPlaceName(Location(lat = it.latitude, lng = it.longitude)).also { name ->
                    _uiState.update { state -> state.copy(fromText = name) }
                }
            }
        }
        endPoint?.also {
            launchBack {
                fetchPlaceName(Location(lat = it.latitude, lng = it.longitude)).also { name ->
                    _uiState.update { state -> state.copy(toText = name) }
                }
            }
        }
        _uiState.update { state ->
            state.copy(
                mapData = state.mapData.copy(
                    startPoint = startPoint ?: state.mapData.startPoint,
                    endPoint = endPoint ?: state.mapData.endPoint,
                    currentLocation = currentLocation ?: state.mapData.currentLocation
                )
            )
        }
    }

    fun setLastLocation(lat: Double, lng: Double) {
        _uiState.update { state ->
            state.copy(mapData = state.mapData.copy(currentLocation = GoogleLatLng(lat, lng)))
        }
    }

    fun setFromText(fromText: String) {
        _uiState.update { state ->
            state.copy(fromText = fromText)
        }
    }

    fun clearFromText() {
        _uiState.update { state ->
            state.copy(
                mapData = state.mapData.copy(
                    startPoint = null,
                    duration = null,
                    durationText = "",
                    distance = null,
                    distanceText = "",
                    durationDistance = "",
                    fare = 0.0,
                    routePoints = emptyList()
                ),
                fromText = ""
            )
        }
    }

    fun setToText(toText: String) {
        _uiState.update { state ->
            state.copy(toText = toText)
        }
    }

    fun clearToText() {
        _uiState.update { state ->
            state.copy(
                mapData = state.mapData.copy(
                    endPoint = null,
                    duration = null,
                    durationText = "",
                    distance = null,
                    distanceText = "",
                    durationDistance = "",
                    fare = 0.0,
                    routePoints = emptyList()
                ),
                toText = ""
            )
        }
    }

    fun updateCurrentLocation(lastLocation: GoogleLatLng) {
        _uiState.update { state ->
            state.copy(mapData = state.mapData.copy(currentLocation = lastLocation))
        }
        launchBack {
            project.pref.updatePref(
                listOf(
                    PreferenceData(PREF_LAST_LATITUDE, lastLocation.latitude.toString()),
                    PreferenceData(PREF_LAST_LONGITUDE, lastLocation.longitude.toString()))
            )
        }
    }

    private fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    data class State(
        val mapData: MapData = MapData(),
        val fromText: String = "",
        val toText: String = "",
        val isProcess: Boolean = false,
    )
}