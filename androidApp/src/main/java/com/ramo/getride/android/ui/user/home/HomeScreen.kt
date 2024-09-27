package com.ramo.getride.android.ui.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramo.getride.android.R
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.ui.LoadingScreen
import com.ramo.getride.android.global.ui.rememberExitToApp
import com.ramo.getride.android.global.ui.rememberTaxi
import com.ramo.getride.android.ui.common.MapScreen
import com.ramo.getride.data.model.UserPref
import com.ramo.getride.global.base.AUTH_SCREEN_ROUTE
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    userPref: UserPref,
    navigateToScreen: suspend (Screen, String) -> Unit,
    navigateHome: suspend (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    theme: Theme = koinInject()
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    val scaffoldState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
    val taxiIcon = rememberTaxi(theme.textForPrimaryColor)
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
                                scaffoldState.showSnackbar(message = "Failed")
                            }
                        }
                    }
                )
            }
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(scaffoldState) {
                    Snackbar(it, containerColor = theme.backDarkSec, contentColor = theme.textColor)
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text = "Ride", color = theme.textForPrimaryColor) },
                    onClick = {

                    },
                    containerColor = theme.primary,
                    shape = RoundedCornerShape(15.dp),
                    expanded = true,
                    icon = {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = taxiIcon,
                            contentDescription = "Ride",
                            tint = theme.textForPrimaryColor
                        )
                    }
                )
            }
        ) { padding ->
            BarMainScreen(userPref = userPref) {
                scope.launch {
                    drawerState.open()
                }
            }
            Column(modifier = Modifier.padding(padding)) {
                Spacer(Modifier.height(60.dp))
                MapScreen(state.routePoints, viewModel::fetchRoute)
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
        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
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
