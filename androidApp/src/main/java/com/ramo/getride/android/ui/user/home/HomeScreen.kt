package com.ramo.getride.android.ui.user.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.base.outlinedTextFieldStyle
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.ui.LoadingScreen
import com.ramo.getride.android.global.ui.OnLaunchScreen
import com.ramo.getride.android.global.ui.RatingBar
import com.ramo.getride.android.global.ui.rememberTaxi
import com.ramo.getride.android.ui.common.BarMainScreen
import com.ramo.getride.android.ui.common.HomeUserDrawer
import com.ramo.getride.android.ui.common.MapData
import com.ramo.getride.android.ui.common.MapScreen
import com.ramo.getride.android.ui.common.defaultLocation
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
        position = CameraPosition.fromLatLngZoom(state.mapData.currentLocation ?: defaultLocation, 15F) // Default position
    }

    val popUpSheet: () -> Unit = remember {
        {
            scope.launch { sheetState.bottomSheetState.show() }
        }
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
                            kotlinx.coroutines.coroutineScope {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(com.google.android.gms.maps.model.LatLng(lat, lng), 15F)
                            }
                        }
                    }
                }
            }
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            HomeUserDrawer(theme) {
                scope.launch {
                    drawerState.close()
                }
                viewModel.signOut({
                    scope.launch { navigateHome(AUTH_SCREEN_ROUTE) }
                }) {
                    scope.launch {
                        sheetState.snackbarHostState.showSnackbar(message = "Failed")
                    }
                }
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
                RideSheetDragHandler(state.ride != null, theme)
            },
            sheetContent = {
                state.ride?.also { ride ->
                    RideSheet(ride = ride, theme = theme, cancelRide = {
                        viewModel.cancelRideFromUser(ride = ride)
                    }, submitFeedback = {
                        viewModel.submitFeedback(ride.driverId, it)
                    }) {
                        viewModel.clearRide()
                    }
                } ?: MapSheetUser(userId = userPref.id, viewModel = viewModel, state = state, refreshScope = refreshScope, theme = theme) { txt ->
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
                    updateCurrentLocation = { location, update ->
                        viewModel.updateCurrentLocation(location) {
                            update()
                            viewModel.checkForActiveRide(userPref.id, popUpSheet, refreshScope)
                        }
                    },
                    theme = theme
                ) { start, end ->
                    viewModel.setMapMarks(startPoint = start, endPoint = end, invoke = refreshScope)
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
                    viewModel.acceptProposal(userId = userPref.id, rideRequest = rideRequest, proposal = proposal, invoke = popUpSheet) {
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
fun MapSheetUser(userId: Long, viewModel: HomeViewModel, state: HomeViewModel.State, refreshScope: (MapData) -> Unit, theme: Theme, snakeBar: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    val focusToRequester = remember { FocusRequester() }
    Column(Modifier.padding(start = 20.dp, end = 20.dp)) {
        Spacer(Modifier.height(5.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = state.mapData.durationDistance,
                color = theme.textColor, modifier = Modifier.padding(),
                fontSize = 20.sp
            )
            if (state.fare != 0.0) {
                Text(
                    text = "$${state.fare}",//Price:
                    color = theme.textColor, modifier = Modifier.padding(),
                    fontSize = 20.sp
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.fromText,
            onValueChange = viewModel::setFromText,
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(text = "From", fontSize = 14.sp) },
            label = { Text(text = "Ride From", fontSize = 14.sp) },
            singleLine = true,
            colors = theme.outlinedTextFieldStyle(),
            trailingIcon = {
                if (state.fromText.isNotEmpty()) {
                    if (state.fromText == state.mapData.fromText) {
                        IconButton(onClick = viewModel::clearFromText) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear From text"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            viewModel.searchForLocationOfPlaceFrom(state.fromText)
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search for From place"
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.searchForLocationOfPlaceFrom(state.fromText)
                    focusManager.clearFocus()
                }
            )
        )
        if (state.locationsFrom.isNotEmpty()) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = viewModel::clearFromList,
                modifier = Modifier.fillMaxWidth()
            ) {
                state.locationsFrom.forEach { locationFrom ->
                    DropdownMenuItem(
                        text = { Text(locationFrom.title) },
                        onClick = {
                            viewModel.setFrom(locationFrom) { mapData ->
                                refreshScope(mapData)
                            }
                            focusManager.clearFocus() // Close keyboard and set text
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(5.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusToRequester),
            value = state.toText,
            onValueChange = viewModel::setToText,
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(text = "To", fontSize = 14.sp) },
            label = { Text(text = "To", fontSize = 14.sp) },
            singleLine = true,
            colors = theme.outlinedTextFieldStyle(),
            trailingIcon = {
                if (state.toText.isNotEmpty()) {
                    if (state.toText == state.mapData.toText) {
                        IconButton(onClick = viewModel::clearToText) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear To text"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            viewModel.searchForLocationOfPlaceTo(state.toText)
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search for To place"
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.searchForLocationOfPlaceTo(state.toText)
                    focusManager.clearFocus()
                }
            )
        )
        if (state.locationsTo.isNotEmpty()) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = viewModel::clearToList,
                modifier = Modifier.fillMaxWidth()
            ) {
                state.locationsTo.forEach { locationTo ->
                    DropdownMenuItem(
                        text = { Text(locationTo.title) },
                        onClick = {
                            viewModel.setTo(locationTo) { mapData ->
                                refreshScope(mapData)
                            }
                            focusManager.clearFocus() // Close keyboard and set text
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding()) {
            Spacer(Modifier)
            ExtendedFloatingActionButton(
                text = { Text(text = "Get Ride", color = theme.textForPrimaryColor) },
                onClick = {
                    if (state.mapData.startPoint != null && state.mapData.endPoint != null) {
                        if (userId != 0L) {
                            viewModel.pushRideRequest(userId = userId)
                        } else {
                            snakeBar("Something Wrong")
                        }
                    } else {
                        snakeBar("Pick destination, Please")
                    }
                },
                containerColor = theme.primary,
                shape = RoundedCornerShape(15.dp),
                expanded = state.mapData.startPoint != null && state.mapData.endPoint != null,
                icon = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = rememberTaxi(theme.textForPrimaryColor),
                        contentDescription = "Ride",
                        tint = theme.textForPrimaryColor
                    )
                }
            )
        }
        Spacer(Modifier.height(15.dp))
    }
}

@Composable
fun RideSheet(ride: Ride, theme: Theme, cancelRide: () -> Unit, submitFeedback: (Float) -> Unit, clearRide: () -> Unit) {
    val statusTitle: String = when (ride.status) {
        0 -> { // To Update Status To 1
            "${ride.driverName} is getting ready to go."
        }
        1 -> { // To Update Status To 2
            "${ride.driverName} on way"
        }
        2 -> { // To Update Status To 3
            "${ride.driverName} waiting for you"
        }
        3 -> { // To Update Status To 4
            "Ride Started"
        }
        4 -> { // To Update Status To 4
            "Give ${ride.driverName} feedback"
        }
        else -> "Canceled"
    }
    Column(
        Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
    ) {
        Spacer(Modifier.height(5.dp))
        Text(
            text = statusTitle,
            color = theme.textColor, modifier = Modifier.padding(),
            fontSize = 18.sp
        )
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding()
        ) {
            Text(
                text = ride.durationDistance,
                color = theme.textColor, modifier = Modifier.padding(),
                fontSize = 16.sp
            )
            Spacer(Modifier)
            Text(
                text = "Fare: $${ride.fare}",
                color = theme.textColor, modifier = Modifier.padding(),
                fontSize = 16.sp
            )
            Spacer(Modifier)
        }
        Spacer(Modifier.height(10.dp))
        if (ride.status == 4) {
            RatingBar(rating = 0F, modifier = Modifier.padding(), onRate = submitFeedback)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (ride.status != -2 && ride.status != -1 && ride.status != 3 && ride.status != 4) {
                Button(
                    onClick = {
                        cancelRide()
                    },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Red, contentColor = Color.Black)
                ) {
                    Text(
                        text = "Cancel Ride",
                        color = Color.Black
                    )
                }
                Spacer(Modifier.width(5.dp))
            }
            if (ride.status == 4) {
                Spacer(Modifier)
                Button(
                    onClick = clearRide,
                    colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Green, contentColor = Color.Black),
                    contentPadding = PaddingValues(start = 30.dp, top = 7.dp, end = 30.dp, bottom = 7.dp)
                ) {
                    Text(
                        text = "Close",
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(Modifier.height(5.dp))
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
        LazyColumn(
            Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth()
                .height(350.dp)) {
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
                Spacer(Modifier.height(10.dp))
            }
            items(rideRequest.driverProposals) { proposal ->
                Column(Modifier.padding()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding()) {
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
                    if (proposal.rate != 0F) {
                        Spacer(Modifier.height(5.dp))
                        RatingBar(rating = proposal.rate, starSize = 20.dp, modifier = Modifier.padding())
                    }
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

@Composable
fun RideSheetDragHandler(isUserHaveRide: Boolean, theme: Theme) {
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
        if (isUserHaveRide) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp), text = "Ride Status", color = theme.textColor,
                fontSize = 16.sp
            )
        }
        Spacer(Modifier.height(10.dp))
    }
}
