package com.ramo.getride.android

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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ramo.getride.android.global.base.MyApplicationTheme
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.ui.OnLaunchScreenScope
import com.ramo.getride.android.global.ui.rememberSocial
import com.ramo.getride.android.ui.sign.AuthScreen
import com.ramo.getride.global.base.AUTH_SCREEN_ROUTE
import com.ramo.getride.global.base.HOME_SCREEN_ROUTE
import com.ramo.getride.global.base.SPLASH_SCREEN_ROUTE
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
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
                        SplashScreen(navigateHome = navigateHome, appViewModel = appViewModel)
                    }
                    composable(route = AUTH_SCREEN_ROUTE) {
                        AuthScreen(appViewModel = appViewModel, navigateHome = navigateHome)
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
    theme: Theme = koinInject(),
) {
    val scope = rememberCoroutineScope()
    OnLaunchScreenScope {
        appViewModel.findUserLive {
            scope.launch {
                navigateHome(if (it == null) AUTH_SCREEN_ROUTE else HOME_SCREEN_ROUTE)
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
                    imageVector = rememberSocial(),
                    contentScale = ContentScale.Fit,
                    contentDescription = "AppIcon",
                )
            }
        }
    }
}
