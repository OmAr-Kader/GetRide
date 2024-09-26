package com.ramo.getride.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.global.base.AUTH_DRIVER_SCREEN_ROUTE
import com.ramo.getride.global.base.AUTH_SCREEN_ROUTE
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun TempScreen(navigateHome: suspend (String) -> Unit, theme: Theme = koinInject()) {
    val scope = rememberCoroutineScope()
    val scaffoldState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(scaffoldState) {
                Snackbar(it, containerColor = theme.backDarkSec, contentColor = theme.textColor)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    scope.launch {
                        navigateHome(AUTH_SCREEN_ROUTE)
                    }
                },
                modifier = Modifier.width(80.dp).height(80.dp)
            ) {
                Text(
                    text = "User",
                    color = theme.textColor
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        navigateHome(AUTH_DRIVER_SCREEN_ROUTE)
                    }
                },
                modifier = Modifier.width(80.dp).height(80.dp)
            ) {
                Text(
                    text = "Driver",
                    color = theme.textColor
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {

                },
                modifier = Modifier.width(80.dp).height(80.dp)
            ) {
                Text(
                    text = "Admin",
                    color = theme.textColor
                )
            }
        }
    }
}

