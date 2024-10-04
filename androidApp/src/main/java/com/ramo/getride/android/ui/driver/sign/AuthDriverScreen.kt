package com.ramo.getride.android.ui.driver.sign

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramo.getride.android.AppViewModel
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.base.outlinedTextFieldStyle
import com.ramo.getride.android.global.ui.AnimatedText
import com.ramo.getride.android.global.ui.LoadingScreen
import com.ramo.getride.global.base.HOME_SCREEN_DRIVER_ROUTE
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AuthDriverScreen(appViewModel: AppViewModel, navigateHome: suspend (String) -> Unit, viewModel: AuthDriverViewModel = koinViewModel(), theme: Theme = koinInject()) {
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
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedText(
                    if (state.isLoginScreen) "Login" else "Sign Up"
                ) { str ->
                    Text(
                        text = str,
                        color = theme.textColor,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    AnimatedVisibility(
                        visible = !state.isLoginScreen
                    ) {
                        Column {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.name,
                                onValueChange = viewModel::setName,
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text(text = "Enter your Name", fontSize = 14.sp) },
                                label = { Text(text = "Name", fontSize = 14.sp) },
                                singleLine = true,
                                colors = theme.outlinedTextFieldStyle(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.phone,
                                onValueChange = viewModel::setPhone,
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text(text = "Enter your Phone", fontSize = 14.sp) },
                                label = { Text(text = "Phone", fontSize = 14.sp) },
                                singleLine = true,
                                colors = theme.outlinedTextFieldStyle(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.carModule,
                                onValueChange = viewModel::setCarModule,
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text(text = "Enter your Car Module", fontSize = 14.sp) },
                                label = { Text(text = "Car Module", fontSize = 14.sp) },
                                singleLine = true,
                                colors = theme.outlinedTextFieldStyle(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.carNumber,
                                onValueChange = viewModel::setCarNumber,
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text(text = "Enter your Car Plate", fontSize = 14.sp) },
                                label = { Text(text = "Car Plate", fontSize = 14.sp) },
                                singleLine = true,
                                colors = theme.outlinedTextFieldStyle(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.carColor,
                                onValueChange = viewModel::setCarColor,
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text(text = "Enter your Car Color", fontSize = 14.sp) },
                                label = { Text(text = "Car Color", fontSize = 14.sp) },
                                singleLine = true,
                                colors = theme.outlinedTextFieldStyle(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = viewModel::setEmail,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(text = "Enter your email", fontSize = 14.sp) },
                    label = { Text(text = "Email", fontSize = 14.sp) },
                    singleLine = true,
                    colors = theme.outlinedTextFieldStyle(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = viewModel::setPassword,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(text = "Enter password", fontSize = 14.sp) },
                    label = { Text(text = "Password", fontSize = 14.sp) },
                    singleLine = true,
                    colors = theme.outlinedTextFieldStyle(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (state.isLoginScreen) {
                            viewModel.loginDriver({
                                appViewModel.findUser {
                                    viewModel.setIsProcess(false)
                                    scope.launch {
                                        if (it != null) {
                                            navigateHome(HOME_SCREEN_DRIVER_ROUTE)
                                        } else {
                                            scope.launch { scaffoldState.showSnackbar("Failed") }
                                        }
                                    }
                                }
                            }) {
                                scope.launch {
                                    scaffoldState.showSnackbar("Failed")
                                }
                            }
                        } else {
                            viewModel.createNewDriver({
                                appViewModel.findUser {
                                    viewModel.setIsProcess(false)
                                    scope.launch {
                                        if (it != null) {
                                            navigateHome(HOME_SCREEN_DRIVER_ROUTE)
                                        } else {
                                            scope.launch { scaffoldState.showSnackbar("Failed") }
                                        }
                                    }
                                }
                            }) {
                                scope.launch { scaffoldState.showSnackbar("Failed") }
                            }
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedText(
                        if (state.isLoginScreen) "Login" else "Sign Up"
                    ) { str ->
                        Text(
                            text = str,
                            color = theme.textColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = viewModel::toggleScreen,
                ) {
                    AnimatedText(
                        if (state.isLoginScreen) "Don't have an account? Sign Up" else "Already have an account? Login"
                    ) { str ->
                        Text(
                            text = str,
                            color = theme.textColor
                        )
                    }
                }
            }
        }
        LoadingScreen(isLoading = state.isProcess, theme = theme)
    }
}
