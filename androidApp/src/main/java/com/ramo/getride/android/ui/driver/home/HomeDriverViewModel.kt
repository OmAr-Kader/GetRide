package com.ramo.getride.android.ui.driver.home

import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.data.map.GoogleLocation
import com.ramo.getride.data.map.fetchAndDecodeRoute
import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.PreferenceData
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest
import com.ramo.getride.data.supaBase.signOutAuth
import com.ramo.getride.data.util.REALM_SUCCESS
import com.ramo.getride.data.util.toDriverCannotSubmit
import com.ramo.getride.di.Project
import com.ramo.getride.global.base.PREF_LAST_LATITUDE
import com.ramo.getride.global.base.PREF_LAST_LONGITUDE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeDriverViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private var jobDriverRideRequests: kotlinx.coroutines.Job? = null

    private var jobRideRequest: kotlinx.coroutines.Job? = null
    private var jobRideInitial: kotlinx.coroutines.Job? = null
    private var jobRide: kotlinx.coroutines.Job? = null

    fun loadRequests(driverId: Long, currentLocation: Location, invoke: () -> Unit) {
        jobDriverRideRequests = launchBack {
            project.ride.getNearRideRequestsForDriver(currentLocation) { requests ->
                requests.find {
                    it.isDriverChosen(driverId) && it.chosenRide != 0L
                }?.also {
                    fetchRide(it.chosenRide, invoke)
                }
                requests.find { request ->
                    request.driverProposals.any { it.driverId == driverId }
                }.let { proposalHadSubmit ->
                    if (proposalHadSubmit != null) {
                        requests.toDriverCannotSubmit(proposalHadSubmit.id)
                    } else {
                        requests
                    }
                }.also { requestsForDriver ->
                    _uiState.update { state ->
                        state.copy(requests = requestsForDriver)
                    }
                }
            }
        }
    }

    private fun fetchRide(rideId: Long, invoke: () -> Unit) {
        jobRide = launchBack {
            project.ride.getRideById(rideId) { ride ->
                _uiState.update { state ->
                    if (state.ride == null) {
                        invoke()
                    }
                    state.copy(ride = ride, isProcess = false)
                }
            }
        }
    }

    fun checkForActiveRide(driverId: Long, invoke: () -> Unit) {
        jobRideInitial = launchBack {
            project.ride.getActiveRideForDriver(driverId = driverId) { ride ->
                _uiState.update { state ->
                    if (state.ride == null) {
                        invoke()
                    }
                    state.copy(ride = ride, isProcess = false)
                }
            }
        }
    }

    fun setLastLocation(lat: Double, lng: Double) {
        _uiState.update { state ->
            state.copy(mapData = state.mapData.copy(currentLocation = GoogleLatLng(lat, lng)))
        }
    }

    fun showOnMap(start: GoogleLatLng, end: GoogleLatLng, invoke: (MapData) -> Unit) {
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

    fun submitProposal(rideRequestId: Long, driverId: Long, fare: Double, location: Location) {
        setIsProcess(true)
        launchBack {
            project.ride.editAddDriverProposal(
                rideRequestId = rideRequestId, RideProposal(driverId = driverId, driverName = "Driver Name", rate = 5.0F, fare = fare, currentDriver = location)
            ).also {
                setIsProcess(false)
            }
        }
    }

    fun cancelProposal(rideRequestId: Long, driverId: Long,) {
        setIsProcess(true)
        launchBack {
            project.ride.editRemoveDriverProposal(rideRequestId = rideRequestId, driverId = driverId).also {
                setIsProcess(false)
            }
        }
    }

    fun updateCurrentLocation(currentLocation: GoogleLatLng) {
        _uiState.update { state ->
            state.copy(mapData = state.mapData.copy(currentLocation = currentLocation))
        }
        launchBack {
            project.pref.updatePref(
                listOf(
                    PreferenceData(PREF_LAST_LATITUDE, currentLocation.latitude.toString()),
                    PreferenceData(PREF_LAST_LONGITUDE, currentLocation.longitude.toString())
                )
            )
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

    private fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    override fun onCleared() {
        jobDriverRideRequests?.cancel()
        jobRideRequest?.cancel()
        jobRideInitial?.cancel()
        jobRide?.cancel()
        jobDriverRideRequests = null
        jobRideRequest = null
        jobRideInitial = null
        jobRide =null
        super.onCleared()
    }

    data class State(
        val requests: List<RideRequest> = emptyList(),
        val mapData: MapData = MapData(),
        val ride: Ride? = null,
        val isProcess: Boolean = true,
    )
}