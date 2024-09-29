package com.ramo.getride.android.ui.user.home

import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.data.map.GoogleLocation
import com.ramo.getride.data.map.fetchAndDecodeRoute
import com.ramo.getride.data.map.fetchPlaceName
import com.ramo.getride.data.map.searchForPlaceLocation
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
import com.ramo.getride.global.util.dateNow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

class HomeViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private var jobRideRequest: kotlinx.coroutines.Job? = null

    private fun fetchRoute(start: GoogleLatLng, end: GoogleLatLng, invoke: (MapData) -> Unit) {
        setIsProcess(true)
        launchBack {
            fetchAndDecodeRoute(
                GoogleLocation(lat = start.latitude, lng = start.longitude),
                GoogleLocation(lat = end.latitude, lng = end.longitude)
            ) { routePoints, duration, distance ->
                routePoints?.also { routes ->
                    PolylineOptions()
                        .addAll(PolyUtil.decode(routes))
                        .points.also { points ->
                            (duration?.convertSecondsToHoursMinutes() ?: "").also { durationText ->
                                (distance?.convertMetersToKmAndMeters() ?: "").also { distanceText ->
                                    (if (distanceText.isEmpty()) "" else durationText + " (${distanceText})").also { durationDistance ->
                                        _uiState.update { state ->
                                            state.mapData.copy(
                                                routePoints = points,
                                                duration = duration,
                                                durationText = durationText,
                                                distance = distance,
                                                distanceText = distanceText,
                                                durationDistance = durationDistance,
                                                fare = calculateFare(duration = duration, distance = distance)
                                            ).also(invoke).let { newMapData ->
                                                state.copy(
                                                    mapData = newMapData,
                                                    isProcess = false
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                } ?: setIsProcess(false)
            }
        }
    }

    fun searchForLocationOfPlaceFrom(fromText: String) {
        launchBack {
            searchForPlaceLocation(placeName = fromText).also {
                _uiState.update { state ->
                    state.copy(locationsFrom = it)
                }
            }
        }
    }

    fun searchForLocationOfPlaceTo(toText: String) {
        launchBack {
            searchForPlaceLocation(placeName = toText).also {
                _uiState.update { state ->
                    state.copy(locationsTo = it)
                }
            }
        }
    }

    private suspend fun Int.checkDeleting(invoke: suspend () -> Unit, failed: suspend () -> Unit) {
        if (this@checkDeleting == REALM_SUCCESS) {
            invoke.invoke()
        } else {
            failed.invoke()
        }
    }

    fun setMapMarks(startPoint: GoogleLatLng? = null, endPoint: GoogleLatLng? = null, currentLocation: GoogleLatLng? = null, invoke: (MapData) -> Unit) {
        checkForPlacesNames(startPoint = startPoint, endPoint = endPoint)
        checkForRoutes(startPoint = startPoint, endPoint = endPoint, invoke = invoke)
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

    private fun checkForRoutes(startPoint: GoogleLatLng?, endPoint: GoogleLatLng?, invoke: (MapData) -> Unit) {
        uiState.value.also { state ->
            (startPoint ?: state.mapData.startPoint)?.also { startPoint ->
                (endPoint ?: state.mapData.endPoint)?.also { endPoint ->
                    fetchRoute(start = startPoint, end = endPoint, invoke = invoke)
                }
            }
        }
    }

    private fun checkForPlacesNames(startPoint: GoogleLatLng?, endPoint: GoogleLatLng?) {
        startPoint?.also {
            launchBack {
                fetchPlaceName(GoogleLocation(lat = it.latitude, lng = it.longitude)).also { name ->
                    _uiState.update { state -> state.copy(mapData = state.mapData.copy(fromText = name), fromText = name) }
                }
            }
        }
        endPoint?.also {
            launchBack {
                fetchPlaceName(GoogleLocation(lat = it.latitude, lng = it.longitude)).also { name ->
                    _uiState.update { state -> state.copy(mapData = state.mapData.copy(toText = name), toText = name) }
                }
            }
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

    fun setFrom(from: Location, invoke: (MapData) -> Unit) {
        GoogleLatLng(from.latitude, from.longitude).also { startPoint ->
            checkForRoutes(startPoint = startPoint, endPoint = null, invoke = invoke)
            _uiState.update { state ->
                state.copy(
                    mapData = state.mapData.copy(
                        startPoint = startPoint,
                        fromText = from.title,
                    ),
                    fromText = from.title,
                    locationsFrom = emptyList()
                )
            }
        }

    }

    fun clearFromList() {
        _uiState.update { state ->
            state.copy(
                locationsFrom = emptyList()
            )
        }
    }

    fun clearFromText() {
        _uiState.update { state ->
            state.copy(
                mapData = state.mapData.copy(
                    startPoint = null,
                    fromText = "",
                    duration = null,
                    durationText = "",
                    distance = null,
                    distanceText = "",
                    durationDistance = "",
                    fare = 0.0,
                    routePoints = emptyList()
                ),
                fromText = "",
            )
        }
    }

    fun setToText(toText: String) {
        _uiState.update { state ->
            state.copy(toText = toText)
        }
    }

    fun setTo(to: Location, invoke: (MapData) -> Unit) {
        GoogleLatLng(to.latitude, to.longitude).also { endPoint ->
            checkForRoutes(startPoint = null, endPoint = endPoint, invoke = invoke)
            _uiState.update { state ->
                state.copy(
                    mapData = state.mapData.copy(
                        endPoint = endPoint,
                        toText = to.title,
                    ),
                    toText = to.title,
                    locationsTo = emptyList()
                )
            }
        }
    }

    fun clearToList() {
        _uiState.update { state ->
            state.copy(
                locationsTo = emptyList()
            )
        }
    }

    fun clearToText() {
        _uiState.update { state ->
            state.copy(
                mapData = state.mapData.copy(
                    endPoint = null,
                    toText = "",
                    duration = null,
                    durationText = "",
                    distance = null,
                    distanceText = "",
                    durationDistance = "",
                    fare = 0.0,
                    routePoints = emptyList()
                ),
                toText = "",
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

    fun pushRideRequest(userId: Long) {
        setIsProcess(true)
        uiState.value.mapData.also { mapData ->
            launchBack {
                mapData.startPoint?.also { from ->
                    mapData.endPoint?.also { to ->
                        RideRequest(
                            0L,
                            userId = userId,
                            from = Location(from.latitude, from.longitude),
                            to = Location(to.latitude, to.longitude),
                            durationDistance = mapData.durationDistance,
                            fare = mapData.fare,
                        ).also {
                            project.ride.addNewRideRequest(it)?.also { rideRequest ->
                                fetchRequestLive(rideRequest.id)
                            } ?: setIsProcess(false)
                        }
                    }
                }
            }
        }
    }

    /*private fun testDriver(rideId: Long, fare: Double,) {
        launchBack {
            kotlinx.coroutines.delay(2000L)
            kotlinx.coroutines.coroutineScope {
                loggerError(error = fare.toString())
                project.ride.editAddDriverProposal(
                    rideId, RideProposal(driverId = 1, driverName = "Test Driver Name", rate = 5.0F, fare = fare)
                ).also {
                    loggerError(error = it.toString())
                }
            }
            kotlinx.coroutines.delay(4000L)
            kotlinx.coroutines.coroutineScope {
                loggerError(error = fare.toString())
                project.ride.editAddDriverProposal(
                    rideId, RideProposal(driverId = 1, driverName = "Driver Name", rate = 5.0F, fare = fare)
                ).also {
                    loggerError(error = it.toString())
                }
            }
        }
    }*/

    fun cancelRideRequest(rideRequest: RideRequest) {
        _uiState.update { state ->
            state.copy(rideRequest = null, isProcess = false)
        }
        launchBack {
            project.ride.deleteRideRequest(rideRequest.id)
        }
    }

    fun acceptProposal(userId: Long, rideRequest: RideRequest, proposal: RideProposal, failed: () -> Unit) {
        setIsProcess(true)
        launchBack {
            project.ride.addNewRide(
                Ride(
                    userId = userId,
                    driverId = proposal.driverId,
                    from = rideRequest.from,
                    to = rideRequest.to,
                    fare = proposal.fare,
                    status = 0,
                    date = dateNow,
                )
            )?.also { ride ->
                project.ride.deleteRideRequest(rideRequest.id)
                fetchRide(rideId = ride.id)
            } ?: kotlin.run {
                setIsProcess(false)
                failed()
            }
        }
    }

    private fun fetchRequestLive(rideRequestId: Long) {
        jobRideRequest = launchBack {
            project.ride.getRideRequestById(rideRequestId) { rideRequest ->
                _uiState.update { state ->
                    state.copy(rideRequest = rideRequest, isProcess = false)
                }
            }
        }
    }

    private fun fetchRide(rideId: Long) {
        jobRideRequest = launchBack {
            project.ride.getRideById(rideId) { ride ->
                _uiState.update { state ->
                    state.copy(ride = ride, isProcess = false)
                }
            }
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
        val locationsFrom: List<Location> = emptyList(),
        val locationsTo: List<Location> = emptyList(),
        val rideRequest: RideRequest? = null,
        val ride: Ride? = null,
        val isProcess: Boolean = false,
    )
}