package com.ramo.getride.android.ui.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramo.getride.android.R
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.base.generateTheme
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.ui.LoadingScreen
import com.ramo.getride.android.global.ui.OnLaunchScreen
import com.ramo.getride.android.global.ui.RatingBar
import com.ramo.getride.android.global.ui.rememberExitToApp
import com.ramo.getride.android.global.ui.rememberTaxi
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.android.ui.common.MapScreen
import com.ramo.getride.android.ui.common.MapSheetUser
import com.ramo.getride.data.model.Ride
import com.ramo.getride.data.model.RideProposal
import com.ramo.getride.data.model.RideRequest
import com.ramo.getride.data.model.UserPref
import com.ramo.getride.global.base.AUTH_SCREEN_ROUTE
import com.ramo.getride.global.base.PREF_LAST_LATITUDE
import com.ramo.getride.global.base.PREF_LAST_LONGITUDE
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    userPref: UserPref,
    findPreference: (String, (it: String?) -> Unit) -> Unit,
    @Suppress("UNUSED_PARAMETER") navigateToScreen: suspend (Screen, String) -> Unit,
    navigateHome: suspend (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    theme: Theme = koinInject()
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberBottomSheetScaffoldState(rememberStandardBottomSheetState(initialValue = SheetValue.Expanded), SnackbarHostState())
    val sheetModalState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(state.mapData.currentLocation, 15F) // Default position
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
                latitude?.toDoubleOrNull()?.also { lat ->
                    longitude?.toDoubleOrNull()?.also { lng ->
                        viewModel.setLastLocation(lat = lat, lng = lng)
                    }
                }
            }
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight(),
                drawerTonalElevation = 6.dp,
                drawerContainerColor = theme.backDark
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.0.dp),
                    shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
                    color = theme.primary,
                ) {
                    Row(
                        Modifier.padding(start = 16.dp, end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Sociality",
                            color = theme.textForPrimaryColor
                        )
                    }
                }
                NavigationDrawerItem(
                    label = { Text(text = "Sign out") },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = theme.backDark,
                        selectedContainerColor = theme.backDark,
                    ),
                    icon = {
                        Icon(
                            imageVector = rememberExitToApp(theme.textColor),
                            modifier = Modifier.size(25.dp),
                            contentDescription = "Sign out"
                        )
                    },
                    selected = false,
                    onClick = {
                        viewModel.signOut({
                            scope.launch { navigateHome(AUTH_SCREEN_ROUTE) }
                        }) {
                            scope.launch {
                                sheetState.snackbarHostState.showSnackbar(message = "Failed")
                            }
                        }
                    }
                )
            }
            LoadingScreen(isLoading = state.isProcess, theme = theme)
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
                Surface(
                    modifier = Modifier
                        .padding(top = 17.dp),
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
                Spacer(Modifier.height(5.dp))
            },
            sheetContent = {
                MapSheetUser( userId = userPref.id,viewModel = viewModel, state = state, refreshScope = refreshScope, theme = theme) { txt ->
                    scope.launch {
                        sheetState.snackbarHostState.showSnackbar(txt)
                    }
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
                    updateCurrentLocation = viewModel::updateCurrentLocation,
                    theme = theme
                ) { start, end, current ->
                    viewModel.setMapMarks(startPoint = start, endPoint = end, currentLocation = current, invoke = refreshScope)
                }
            }
            state.rideRequest?.also { rideRequest ->
                RideRequestSheet(
                    sheetModalState,
                    rideRequest,
                    theme,
                    onDismissRequest = {
                        scope.launch { sheetModalState.expand() }
                    },
                    cancelRideRequest = {
                        state.rideRequest?.let { viewModel.cancelRideRequest(it) }
                    }
                ) { proposal ->
                    // @OmAr-Kader => Test acceptProposal + Add Ride Sheet
                    viewModel.acceptProposal(userId = userPref.id, rideRequest = rideRequest, proposal = proposal) {
                        scope.launch {
                            sheetState.snackbarHostState.showSnackbar("Failed")
                        }
                    }
                }
            }
            LoadingScreen(isLoading = state.isProcess, theme = theme)
        }
    }
}

@Composable
fun BarMainScreen(
    userPref: UserPref,
    theme: Theme = koinInject(),
    openDrawer: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(elevation = 15.dp, spotColor = theme.primary, ambientColor = theme.primary),
        //shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = theme.background),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 15.dp, end = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = openDrawer,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = theme.textColor
                        )
                    }
                    Image(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        painter = painterResource(R.drawable.ic_get_ride),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                    )
                }
                Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                Text("Hi, ${userPref.name}", color = theme.textColor, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun RideRequestSheet(
    sheetModalState: SheetState,
    rideRequest: RideRequest,
    theme: Theme,
    onDismissRequest: () -> Unit,
    cancelRideRequest: () -> Unit,
    acceptProposal: (RideProposal) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetModalState,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        containerColor = theme.backDark,
        contentColor = theme.textColor,
        dragHandle = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(theme.backDark),
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
                        .padding(7.dp), text = "Waiting for Drivers", color = theme.textColor,
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
                Spacer(Modifier.height(5.dp))
            }
        }
    ) {
        LazyColumn(Modifier.padding(start = 20.dp, end = 20.dp).fillMaxWidth().height(350.dp)) {
            item {
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = rideRequest.durationDistance,
                        color = theme.textColor, modifier = Modifier.padding(),
                        fontSize = 20.sp
                    )
                    if (rideRequest.fare != 0.0) {
                        Text(
                            text = "$${rideRequest.fare}",//Price:
                            color = theme.textColor, modifier = Modifier.padding(),
                            fontSize = 20.sp
                        )
                    }
                }
            }
            items(rideRequest.driverProposals) { proposal ->
                Column(Modifier.padding()) {
                    Row(Modifier.fillMaxWidth().padding()) {
                        Text(
                            text = proposal.driverName,
                            color = theme.textColor, modifier = Modifier.padding(),
                            fontSize = 18.sp
                        )
                        Spacer(Modifier)
                        Text(
                            text = "Price: $${proposal.fare}",
                            color = theme.textColor, modifier = Modifier.padding(),
                            fontSize = 18.sp
                        )
                        Spacer(Modifier)
                    }
                    Spacer(Modifier.height(5.dp))
                    RatingBar(rating = proposal.rate, starSize = 20.dp, modifier = Modifier.padding())
                    Spacer(Modifier.height(5.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .fillMaxWidth()
                        .padding()) {
                        Spacer(Modifier)
                        /*Button({

                        }, colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Red, contentColor = Color.Black)) {
                            Text(
                                text = "Reject",
                                color = Color.Black
                            )
                        }*/
                        Button(
                            onClick = {
                                acceptProposal(proposal)
                            },
                            colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Green, contentColor = Color.Black),
                            contentPadding = PaddingValues(start = 30.dp, top = 7.dp, end = 30.dp, bottom = 7.dp)
                        ) {
                            Text(
                                text = "Accept",
                                color = Color.Black
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier)
            ExtendedFloatingActionButton(
                text = { Text(text = "Cancel Ride", color = theme.textForPrimaryColor) },
                onClick = cancelRideRequest,
                containerColor = theme.primary,
                shape = RoundedCornerShape(35.dp),
                expanded = true,
                icon = {

                }
            )
        }
    }
}