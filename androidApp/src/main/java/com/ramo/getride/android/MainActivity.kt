package com.ramo.getride.android

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ramo.getride.android.global.base.MyApplicationTheme
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.ui.OnLaunchScreen
import com.ramo.getride.android.global.util.isTablet
import com.ramo.getride.android.ui.driver.home.HomeDriverScreen
import com.ramo.getride.android.ui.driver.sign.AuthDriverScreen
import com.ramo.getride.android.ui.user.home.HomeScreen
import com.ramo.getride.android.ui.user.sign.AuthScreen
import com.ramo.getride.global.base.AUTH_SCREEN_DRIVER_ROUTE
import com.ramo.getride.global.base.AUTH_SCREEN_ROUTE
import com.ramo.getride.global.base.HOME_SCREEN_DRIVER_ROUTE
import com.ramo.getride.global.base.HOME_SCREEN_ROUTE
import com.ramo.getride.global.base.SPLASH_SCREEN_ROUTE
import com.ramo.getride.global.base.TEMP_IS_DRIVER
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermission()
        setContent {
            Main(isTablet) // TEMP_IS_DRIVER
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }
}

@Composable
fun Main(isDriverMode: Boolean) {
    val theme: Theme = koinInject()
    val appViewModel: AppViewModel = koinViewModel()
    val stateApp by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navigateHome: suspend (String) -> Unit = { route ->
        navController.navigate(route = route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }
    val navigateTo: suspend (String) -> Unit = { route ->
        navController.navigate(route = route)
    }
    val findPreference: (String, (it: String?) -> Unit) -> Unit = appViewModel::findPrefString
    val navigateToScreen: suspend (Screen, String) -> Unit = { screen , route ->
        appViewModel.writeArguments(screen)
        kotlinx.coroutines.coroutineScope {
            navController.navigate(route = route)
        }
    }
    val backPress: suspend () -> Unit = {
        navController.navigateUp()
    }

    MyApplicationTheme(theme = theme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = theme.background
        ) {
            Surface(
                color = theme.background
            ) {
                NavHost(
                    navController = navController,
                    startDestination = SPLASH_SCREEN_ROUTE
                ) {
                    composable(route = SPLASH_SCREEN_ROUTE) {
                        SplashScreen(navigateHome = navigateHome, appViewModel = appViewModel, isDriverMode = isDriverMode)
                    }
                    composable(route = AUTH_SCREEN_ROUTE) {
                        AuthScreen(appViewModel = appViewModel, navigateHome = navigateHome)
                    }
                    composable(route = AUTH_SCREEN_DRIVER_ROUTE) {
                        AuthDriverScreen(appViewModel = appViewModel, navigateHome = navigateHome)
                    }
                    composable(route = HOME_SCREEN_ROUTE) {
                        HomeScreen(stateApp.userPref, findPreference = findPreference, navigateToScreen = navigateToScreen, navigateHome =navigateHome)
                    }
                    composable(route = HOME_SCREEN_DRIVER_ROUTE) {
                        HomeDriverScreen(stateApp.userPref, findPreference = findPreference, navigateToScreen = navigateToScreen, navigateHome =navigateHome)
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    navigateHome: suspend (String) -> Unit,
    appViewModel: AppViewModel,
    isDriverMode: Boolean,
    theme: Theme = koinInject(),
) {
    val scope = rememberCoroutineScope()
    OnLaunchScreen {
        appViewModel.findUserLive {
            scope.launch {
                navigateHome(
                    if (it == null) (if (isDriverMode) AUTH_SCREEN_DRIVER_ROUTE else AUTH_SCREEN_ROUTE)
                    else (if (isDriverMode) HOME_SCREEN_DRIVER_ROUTE else HOME_SCREEN_ROUTE)
                )
            }
        }
    }
    Surface(color = theme.background) {
        androidx.compose.foundation.layout.Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
            AnimatedVisibility(
                visible = true,
                modifier = Modifier
                    .size(100.dp),
                enter = fadeIn(initialAlpha = 0.3F) + expandIn(expandFrom = androidx.compose.ui.Alignment.Center),
                label = "AppIcon"
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_get_ride),
                    contentScale = ContentScale.Fit,
                    contentDescription = "AppIcon",
                )
            }
        }
    }
}
