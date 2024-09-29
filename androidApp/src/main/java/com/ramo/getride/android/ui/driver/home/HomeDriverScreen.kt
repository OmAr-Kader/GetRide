package com.ramo.getride.android.ui.driver.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.base.generateTheme
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.ui.LoadingScreen
import com.ramo.getride.android.ui.common.MapScreen
import com.ramo.getride.data.model.UserPref
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeDriverScreen(
    userPref: UserPref,
    findPreference: (String, (it: String?) -> Unit) -> Unit,
    navigateToScreen: suspend (Screen, String) -> Unit,
    navigateHome: suspend (String) -> Unit,
    viewModel: HomeDriverViewModel = koinViewModel(),
    theme: Theme = koinInject()
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
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
            //MapScreen()
        }
        LoadingScreen(isLoading = state.isProcess, theme = theme)
    }
}

@Preview
@Composable
fun RideRequestSheet(theme: Theme = generateTheme(true), cancelRideRequest: () -> Unit = {}) {
    val sheetModalState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    ModalBottomSheet(
        onDismissRequest = cancelRideRequest,
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
        LazyColumn(Modifier.padding(start = 20.dp, end = 20.dp).defaultMinSize(minHeight = 200.dp)) {
            item {
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}