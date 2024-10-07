package com.ramo.getride.android.ui.driver.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.ui.AnimatedText
import com.ramo.getride.android.global.ui.LoadingScreen
import com.ramo.getride.android.global.ui.OnLaunchScreen
import com.ramo.getride.android.ui.common.BarMainScreen
import com.ramo.getride.android.ui.common.HomeUserDrawer
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.android.ui.common.MapScreen
import com.ramo.getride.android.ui.common.defaultLocation
import com.ramo.getride.android.ui.common.toGoogleLatLng
import com.ramo.getride.android.ui.common.toLocation
import com.ramo.getride.data.model.Location
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest
import com.ramo.getride.data.model.UserPref
import com.ramo.getride.global.base.AUTH_SCREEN_DRIVER_ROUTE
import com.ramo.getride.global.base.PREF_LAST_LATITUDE
import com.ramo.getride.global.base.PREF_LAST_LONGITUDE
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeDriverScreen(
    userPref: UserPref,
    findPreference: (String, (it: String?) -> Unit) -> Unit,
    @Suppress("UNUSED_PARAMETER") navigateToScreen: suspend (Screen, String) -> Unit,
    navigateHome: suspend (String) -> Unit,
    viewModel: HomeDriverViewModel = koinViewModel(),
    theme: Theme = koinInject()
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberBottomSheetScaffoldState(rememberStandardBottomSheetState(), SnackbarHostState())
    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(state.mapData.currentLocation ?: defaultLocation, 15F) // Default position
    }

    val popUpSheet: () -> Unit = {
        scope.launch { sheetState.bottomSheetState.show() }
    }
    val refreshScope: (MapData) -> Unit = remember {
        { mapData ->
            scope.launch {
                val bounds = LatLngBounds.Builder().also { boundsBuilder ->
                    listOf(mapData.startPoint, mapData.endPoint, mapData.currentLocation).forEach {
                        it?.let { it1 -> boundsBuilder.include(it1) }
                    }
                }.build()
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }
    OnLaunchScreen {
        findPreference(PREF_LAST_LATITUDE) { latitude ->
            findPreference(PREF_LAST_LONGITUDE) { longitude ->
                latitude?.toDoubleOrNull()?.let { lat ->
                    longitude?.toDoubleOrNull()?.also { lng ->
                        scope.launch {
                            kotlinx.coroutines.coroutineScope { viewModel.setLastLocation(lat = lat , lng = lng) }
                            kotlinx.coroutines.coroutineScope { viewModel.checkForActiveRide(userPref.id, popUpSheet) }
                        }
                    }
                } ?: scope.launch {
                    kotlinx.coroutines.coroutineScope { viewModel.checkForActiveRide(userPref.id, popUpSheet) }
                }
            }
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            HomeUserDrawer(theme) {
                viewModel.signOut({
                    scope.launch { navigateHome(AUTH_SCREEN_DRIVER_ROUTE) }
                }) {
                    scope.launch {
                        sheetState.snackbarHostState.showSnackbar(message = "Failed")
                    }
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen
    ) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            snackbarHost = {
                SnackbarHost(sheetState.snackbarHostState) {
                    Snackbar(it, containerColor = theme.backDarkSec, contentColor = theme.textColor)
                }
            },
            containerColor = theme.backDark,
            contentColor = theme.textColor,
            sheetDragHandle = {
                DriverDragHandler(state.ride != null, theme)
            },
            sheetContent = {
                state.ride?.also { ride ->
                    SubmittedRideRequestSheet(
                        ride = ride,
                        theme = theme
                    ) { newStatus ->
                        viewModel.updateRide(ride = ride, newStatus = newStatus)
                    }
                } ?: RideRequestsDriverSheet(
                    state.requests,
                    theme,
                    showOnMap = { from, to ->
                        viewModel.showOnMap(start = from.toGoogleLatLng(), end = to.toGoogleLatLng(), invoke = refreshScope)
                    },
                    submitProposal = { request ->
                        state.mapData.currentLocation?.toLocation()?.let {
                            viewModel.submitProposal(rideRequestId = request.id, driverId = userPref.id, fare = request.fare, location = it) {
                                viewModel.showOnMap(start = request.from.toGoogleLatLng(), end = request.to.toGoogleLatLng(), invoke = refreshScope)
                            }
                        }
                    }
                ) { request ->
                    viewModel.cancelProposal(request = request, driverId = userPref.id)
                }
            }
        ) { padding ->
            BarMainScreen(userPref = userPref) {
                scope.launch {
                    drawerState.open()
                }
            }
            Column(modifier = Modifier.padding(padding)) {
                Spacer(Modifier.height(60.dp))
                MapScreen(
                    mapData = state.mapData,
                    cameraPositionState = cameraPositionState,
                    updateCurrentLocation = { currentLocation ->
                        viewModel.updateCurrentLocation(currentLocation)
                        viewModel.loadRequests(userPref.id, currentLocation.toLocation(), popUpSheet)
                    },
                    theme = theme
                ) { _, _ ->

                }
            }
            LoadingScreen(isLoading = state.isProcess, theme = theme)
        }
    }
}

@Composable
fun SubmittedRideRequestSheet(ride: Ride, theme: Theme, updateRideStatus: (status: Int) -> Unit) {
    val actionTitle: String = when (ride.status) {
        0 -> { // To Update Status To 1
            "Going to Your Client"
        }
        1 -> { // To Update Status To 2
            "Reached the destination"
        }
        2 -> { // To Update Status To 3
            "Start Your Ride"
        }
        3 -> { // To Update Status To 4
            "Finish Your Ride"
        }
        4 -> "Submit client feedback"
        else -> "Canceled"
    }
    val targetedAction: () -> Int? = {
        when(ride.status) {
            0 -> 1
            1 -> 2
            2 -> 3
            3 -> 4
            else -> null
        }
    }
    Column(
        Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .height(100.dp)) {
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding()) {
            Text(
                text = ride.durationDistance,
                color = theme.textColor, modifier = Modifier.padding(),
                fontSize = 18.sp
            )
            Spacer(Modifier.defaultMinSize(minWidth = 10.dp))
            Text(
                text = "Fare: $${ride.fare}",
                color = theme.textColor, modifier = Modifier.padding(),
                fontSize = 18.sp
            )
            Spacer(Modifier)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (ride.status == 4) {
                // Feedback UI
            }
            if (ride.status != -2 && ride.status != -1 && ride.status != 3 && ride.status != 4) {
                Button(
                    onClick = {
                        updateRideStatus(-1)
                    },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Red, contentColor = Color.Black)
                ) {
                    Text(
                        text = "Cancel Ride",
                        color = Color.Black
                    )
                }
            }
            Spacer(Modifier)
            Button(
                onClick = {
                    if (ride.status == 4) {
                        // @OmAr-Kader => When Review is submitted, clear Ride
                    } else {
                        targetedAction()?.also { action ->
                            updateRideStatus(action)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Green, contentColor = Color.Black),
                contentPadding = PaddingValues(start = 30.dp, top = 7.dp, end = 30.dp, bottom = 7.dp)
            ) {
                AnimatedText(
                    actionTitle
                ) { str ->
                    Text(
                        text = str,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun RideRequestsDriverSheet(
    requests: List<RideRequest>,
    theme: Theme,
    showOnMap: (from: Location, to: Location) -> Unit,
    submitProposal: (RideRequest) -> Unit,
    cancelSubmitProposal: (RideRequest) -> Unit
) {
    LazyColumn(
        Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .height(350.dp)) {
        items(requests) { request ->
            Column(Modifier.padding()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding()) {
                    Text(
                        text = request.durationDistance,
                        color = theme.textColor, modifier = Modifier.padding(),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier)
                    Text(
                        text = "Fare: $${request.fare}",
                        color = theme.textColor, modifier = Modifier.padding(),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier)
                }
                //Spacer(Modifier.height(5.dp))
                //RatingBar(rating = request.userRate, starSize = 20.dp, modifier = Modifier.padding())
                Spacer(Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .fillMaxWidth()
                        .padding()
                ) {
                    Spacer(Modifier)
                    Button(
                        onClick = {
                            showOnMap(request.from, request.to)
                        },
                        colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Red, contentColor = Color.Black)
                    ) {
                        Text(
                            text = "Show On Map",
                            color = Color.Black
                        )
                    }
                    if (request.isDriverCanSubmit) {
                        Button(
                            onClick = {
                                submitProposal(request)
                            },
                            colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Green, contentColor = Color.Black),
                            contentPadding = PaddingValues(start = 30.dp, top = 7.dp, end = 30.dp, bottom = 7.dp)
                        ) {
                            Text(
                                text = "Submit Proposal",
                                color = Color.Black
                            )
                        }
                    } else if (request.requestHadSubmit) {
                        Button(
                            onClick = {
                                cancelSubmitProposal(request)
                            },
                            colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Green, contentColor = Color.Black),
                            contentPadding = PaddingValues(start = 30.dp, top = 7.dp, end = 30.dp, bottom = 7.dp)
                        ) {
                            Text(
                                text = "Cancel Proposal",
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun DriverDragHandler(isDriverHaveRide: Boolean, theme: Theme) {
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .padding(top = 22.dp),
            color = theme.textGrayColor,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Box(
                Modifier
                    .size(
                        width = 32.dp,
                        height = 4.0.dp
                    )
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp), text = if (isDriverHaveRide) "Ride Status" else "Searching for Ride Requests", color = theme.textColor,
            fontSize = 16.sp
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                trackColor = Color.Transparent,
                color = theme.primary
            )
        }
        Spacer(Modifier.height(10.dp))
    }
}
