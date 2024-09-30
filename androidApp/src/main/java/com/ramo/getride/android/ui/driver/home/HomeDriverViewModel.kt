package com.ramo.getride.android.ui.driver.home

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.android.ui.common.toLocation
import com.ramo.getride.data.map.GoogleLocation
import com.ramo.getride.data.map.fetchAndDecodeRoute
import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.PreferenceData
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest
import com.ramo.getride.data.supaBase.signOutAuth
import com.ramo.getride.data.util.REALM_SUCCESS
import com.ramo.getride.data.util.calculateFare
import com.ramo.getride.data.util.convertMetersToKmAndMeters
import com.ramo.getride.data.util.convertSecondsToHoursMinutes
import com.ramo.getride.di.Project
import com.ramo.getride.global.base.PREF_LAST_LATITUDE
import com.ramo.getride.global.base.PREF_LAST_LONGITUDE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeDriverViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private var jobRideRequest: kotlinx.coroutines.Job? = null

    private fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    private fun loadRequests(currentLocation: Location) {
        jobRideRequest = launchBack {
            project.ride.getNearRideRequestsForDriver(currentLocation) { requests ->
                _uiState.update { state ->
                    state.copy(requests = requests)
                }
            }
        }
    }

    fun showOnMap(start: LatLng, end: LatLng, invoke: (MapData) -> Unit) {
        setIsProcess(true)
        launchBack {
            fetchAndDecodeRoute(
                GoogleLocation(lat = start.latitude, lng = start.longitude),
                GoogleLocation(lat = end.latitude, lng = end.longitude)
            ) { routePoints, _, _ ->
                routePoints?.also { routes ->
                    PolylineOptions()
                        .addAll(PolyUtil.decode(routes))
                        .points.also { points ->
                            _uiState.update { state ->
                                state.mapData.copy(
                                    startPoint = start,
                                    endPoint = end,
                                    routePoints = points,
                                ).also(invoke).let { newMapData ->
                                    state.copy(
                                        mapData = newMapData,
                                        isProcess = false
                                    )
                                }
                            }
                        }
                } ?: setIsProcess(false)
            }
        }
    }

    fun submitProposal(rideId: Long, fare: Double, location: Location) {
        setIsProcess(true)
        launchBack {
            project.ride.editAddDriverProposal(
                rideId, RideProposal(driverId = 1, driverName = "Driver Name", rate = 5.0F, fare = fare, currentDriver = location)
            )
        }
    }

    fun updateCurrentLocation(currentLocation: LatLng) {
        _uiState.update { state ->
            state.copy(mapData = state.mapData.copy(currentLocation = currentLocation))
        }
        loadRequests(currentLocation.toLocation())
        launchBack {
            project.pref.updatePref(
                listOf(
                    PreferenceData(PREF_LAST_LATITUDE, currentLocation.latitude.toString()),
                    PreferenceData(PREF_LAST_LONGITUDE, currentLocation.longitude.toString())
                )
            )
        }
    }

    fun setLastLocation(lat: Double, lng: Double) {
        _uiState.update { state ->
            state.copy(mapData = state.mapData.copy(currentLocation = LatLng(lat, lng)))
        }
    }

    fun updateRide(ride: Ride, newStatus: Int) {
        launchBack {
            project.ride.editRide(ride.copy(status = newStatus))
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
        val requests: List<RideRequest> = emptyList(),
        val mapData: MapData = MapData(),
        val ride: Ride? = null,
        val isProcess: Boolean = false,
    )
}