package com.ramo.getride.android.ui.user.home

import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.android.ui.common.toGoogleLatLng
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
    private var jobRideInitial: kotlinx.coroutines.Job? = null
    private var jobRide: kotlinx.coroutines.Job? = null

    fun checkForActiveRide(userId: Long, invoke: () -> Unit, refreshScope: (MapData) -> Unit) {
        jobRideInitial?.cancel()
        jobRideInitial = launchBack {
            project.ride.getActiveRideForUser(userId = userId) { ride ->
                _uiState.update { state ->
                    if (state.ride == null && ride != null) {
                        invoke()
                    }
                    if (ride?.status == -2) {
                        jobRideInitial?.cancel()
                        jobRideInitial = null
                        state.copy(ride = null, mapData = state.mapData.copy(driverPoint = null), isProcess = false)
                    } else {
                        if (ride != null && state.mapData.routePoints.isEmpty()) {
                            fetchRoute(ride.from.toGoogleLatLng(), ride.to.toGoogleLatLng(), refreshScope)
                        }
                        state.copy(ride = ride, mapData = state.mapData.copy(
                            driverPoint = ride?.currentDriver?.toGoogleLatLng(),
                            startPoint = ride?.from?.toGoogleLatLng(),
                            fromText = ride?.from?.title ?: "",
                            endPoint = ride?.to?.toGoogleLatLng(),
                            toText = ride?.to?.title ?: "",
                        ), isProcess = false)
                    }
                }
            }
        }
    }

    fun cancelRideFromUser(ride: Ride) {
        launchBack {
            project.ride.editRide(ride.copy(status = -2))
        }
    }

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
                                                durationDistance = durationDistance,
                                            ).also(invoke).let { newMapData ->
                                                state.copy(
                                                    mapData = newMapData,
                                                    duration = duration,
                                                    durationText = durationText,
                                                    distance = distance,
                                                    distanceText = distanceText,
                                                    fare = calculateFare(duration = duration, distance = distance),
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

    fun setMapMarks(startPoint: GoogleLatLng? = null, endPoint: GoogleLatLng? = null, invoke: (MapData) -> Unit) {
        checkForPlacesNames(startPoint = startPoint, endPoint = endPoint)
        checkForRoutes(startPoint = startPoint, endPoint = endPoint, invoke = invoke)
        _uiState.update { state ->
            state.copy(
                mapData = state.mapData.copy(
                    startPoint = startPoint ?: state.mapData.startPoint,
                    endPoint = endPoint ?: state.mapData.endPoint,
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
                    durationDistance = "",
                    routePoints = emptyList()
                ),
                duration = null,
                durationText = "",
                distance = null,
                distanceText = "",
                fare = 0.0,
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
                    durationDistance = "",
                    routePoints = emptyList()
                ),
                duration = null,
                durationText = "",
                distance = null,
                distanceText = "",
                fare = 0.0,
                toText = "",
            )
        }
    }

    fun updateCurrentLocation(currentLocation: GoogleLatLng, update: () -> Unit) {
        uiState.value.mapData.also {
            if (!it.isCurrentAlmostSameArea(currentLocation)) {
                update()
                updateCurrentLocationPref(currentLocation)
                _uiState.update { state ->
                    state.copy(mapData = state.mapData.copy(currentLocation = currentLocation))
                }
            }
        }
    }

    private fun updateCurrentLocationPref(currentLocation: GoogleLatLng) {
        launchBack {
            project.pref.updatePref(
                listOf(
                    PreferenceData(PREF_LAST_LATITUDE, currentLocation.latitude.toString()),
                    PreferenceData(PREF_LAST_LONGITUDE, currentLocation.longitude.toString())
                )
            )
        }
    }

    fun submitFeedback(driverId: Long, rate: Float) {
        clearRide()
        launchBack {
            project.driver.addEditDriverRate(driverId = driverId, rate = rate)
        }
    }

    // @OmAr-Kader => fromText = "",
    fun clearRide() {
        jobRide?.cancel()
        jobRideInitial?.cancel()
        jobRide = null
        jobRideInitial = null
        _uiState.update { state ->
            state.copy(
                ride = null,
                mapData = state.mapData.copy(
                    startPoint = null,
                    fromText = "",
                    endPoint = null,
                    toText = "",
                    durationDistance = "",
                    routePoints = emptyList(),
                    driverPoint = null
                ),
                fromText = "",
                duration = null,
                durationText = "",
                distance = null,
                distanceText = "",
                fare = 0.0,
                toText = "",
                isProcess = false,
            )
        }
    }

    fun pushRideRequest(userId: Long) {
        setIsProcess(true)
        uiState.value.also { state ->
            state.mapData.also { mapData ->
                launchBack {
                    mapData.startPoint?.also { from ->
                        mapData.endPoint?.also { to ->
                            RideRequest(
                                0L,
                                userId = userId,
                                from = Location(from.latitude, from.longitude),
                                to = Location(to.latitude, to.longitude),
                                durationDistance = mapData.durationDistance,
                                fare = state.fare,
                                date = dateNow
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
    }

    fun cancelRideRequest(rideRequest: RideRequest) {
        _uiState.update { state ->
            state.copy(rideRequest = null, isProcess = false)
        }
        launchBack {
            project.ride.deleteRideRequest(rideRequest.id)
        }
    }

    fun acceptProposal(userId: Long, rideRequest: RideRequest, proposal: RideProposal, invoke: () -> Unit, failed: () -> Unit) {
        setIsProcess(true)
        launchBack {
            project.ride.addNewRide(
                Ride(
                    userId = userId,
                    driverId = proposal.driverId,
                    from = rideRequest.from,
                    to = rideRequest.to,
                    currentDriver = proposal.currentDriver,
                    fare = proposal.fare,
                    status = 0,
                    date = dateNow,
                    durationDistance = rideRequest.durationDistance,
                    driverName = proposal.driverName,
                )
            )?.let { ride ->
                project.ride.editRideRequest(rideRequest.copy(chosenRide = ride.id, chosenDriver = proposal.driverId))?.also {
                    fetchRide(rideId = ride.id, invoke)
                }
            } ?: kotlin.run {
                setIsProcess(false)
                failed()
            }
        }
    }

    private fun fetchRequestLive(rideRequestId: Long) {
        jobRideRequest?.cancel()
        jobRideRequest = launchBack {
            project.ride.getRideRequestById(rideRequestId) { rideRequest ->
                _uiState.update { state ->
                    state.copy(rideRequest = rideRequest, isProcess = false)
                }
            }
        }
    }

    private fun fetchRide(rideId: Long, invoke: () -> Unit) {
        jobRide?.cancel()
        jobRide = launchBack {
            project.ride.getRideById(rideId) { ride ->
                _uiState.update { state ->
                    if (state.ride == null) {
                        invoke()
                    }
                    jobRideRequest?.cancel()
                    jobRideRequest = null
                    if (ride?.status == -2) {
                        jobRide?.cancel()
                        jobRide = null
                        state.copy(ride = null, mapData = state.mapData.copy(driverPoint = null), isProcess = false)
                    } else {
                        state.copy(
                            ride = ride,
                            mapData = state.mapData.copy(driverPoint = ride?.currentDriver?.toGoogleLatLng()),
                            rideRequest = null,
                            isProcess = false
                        )
                    }
                }
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

    private fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    override fun onCleared() {
        jobRideRequest?.cancel()
        jobRideInitial?.cancel()
        jobRide?.cancel()
        jobRideRequest = null
        jobRideInitial = null
        jobRide =null
        super.onCleared()
    }

    data class State(
        val mapData: MapData = MapData(),
        val duration: Long? = null,
        val durationText: String = "",
        val distance: Long? = null,
        val distanceText: String = "",
        val fare: Double = 0.0,
        val fromText: String = "",
        val toText: String = "",
        val locationsFrom: List<Location> = emptyList(),
        val locationsTo: List<Location> = emptyList(),
        val rideRequest: RideRequest? = null,
        val ride: Ride? = null,
        val isProcess: Boolean = true,
    )
}