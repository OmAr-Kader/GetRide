package com.ramo.getride.android.ui.driver.sign

import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.data.model.Driver
import com.ramo.getride.data.model.PreferenceData
import com.ramo.getride.data.model.UserPref
import com.ramo.getride.data.supaBase.registerAuth
import com.ramo.getride.data.supaBase.signInAuth
import com.ramo.getride.data.supaBase.userInfo
import com.ramo.getride.di.Project
import com.ramo.getride.global.base.PREF_NAME
import com.ramo.getride.global.base.PREF_PROFILE_IMAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthDriverViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun setName(it: String) {
        _uiState.update { state ->
            state.copy(name = it, isErrorPressed = false)
        }
    }

    fun setPhone(it: String) {
        _uiState.update { state ->
            state.copy(phone = it, isErrorPressed = false)
        }
    }

    fun setEmail(it: String) {
        _uiState.update { state ->
            state.copy(email = it, isErrorPressed = false)
        }
    }

    fun setPassword(it: String) {
        _uiState.update { state ->
            state.copy(password = it, isErrorPressed = false)
        }
    }

    fun setCarModule(it: String) {
        _uiState.update { state ->
            state.copy(carModule = it, isErrorPressed = false)
        }
    }

    fun setCarNumber(it: String) {
        _uiState.update { state ->
            state.copy(carNumber = it, isErrorPressed = false)
        }
    }

    fun setCarColor(it: String) {
        _uiState.update { state ->
            state.copy(carColor = it, isErrorPressed = false)
        }
    }

    fun toggleScreen() {
        _uiState.update { state ->
            state.copy(isLoginScreen = !state.isLoginScreen, isErrorPressed = false)
        }
    }

    fun createNewDriver(invoke: () -> Unit, failed: () -> Unit) {
        uiState.value.let { state ->
            if (state.email.isEmpty() || state.password.isEmpty() || state.name.isEmpty() || state.email.isEmpty() || state.phone.isEmpty() || state.carModule.isEmpty() || state.carColor.isEmpty() || state.carNumber.isEmpty()) {
                setIsError(true)
                return
            }
            setIsProcess(true)
            launchBack {
                registerAuth(
                    UserPref(
                        email = state.email,
                        name = state.name
                    ), state.password, invoke = {
                        launchBack {
                            doSignUp(state, invoke, failed)
                        }
                    },
                ) {
                    setIsProcess(false)
                    failed()
                }
            }
        }
    }

    private suspend fun doSignUp(state: State, invoke: () -> Unit, failed: () -> Unit) {
        userInfo()?.let { userBase ->
            project.driver.addNewDriver(
                Driver(
                    authId = userBase.id,
                    driverName = state.name,
                    email = userBase.email,
                    phone = state.phone,
                )
            )?.let { user ->
                project.pref.updatePref(listOf(PreferenceData(PREF_NAME, state.name), PreferenceData(PREF_PROFILE_IMAGE, user.profilePicture))).also {
                    setIsProcess(false)
                    invoke()
                }
            } ?: kotlin.run {
                setIsProcess(false)
                invoke()
            }
        } ?: kotlin.run {
            setIsProcess(false)
            failed()
        }
    }

    private suspend fun doLogin(state: State, invoke: () -> Unit, failed: () -> Unit) {
        signInAuth(state.email, state.password, invoke = {
            launchBack {
                userInfo()?.let {
                    project.driver.getDriverOnAuthId(it.id)?.also { user ->
                        project.pref.updatePref(
                            listOf(PreferenceData(PREF_NAME, state.name), PreferenceData(PREF_PROFILE_IMAGE, user.profilePicture))
                        ).also {
                            setIsProcess(false)
                            invoke()
                        }
                    } ?: kotlin.run {
                        setIsProcess(false)
                        invoke()
                    }
                } ?: kotlin.run {
                    setIsProcess(false)
                    failed()
                }
            }
        }, {
            failed()
        })
    }

    fun loginDriver(invoke: () -> Unit, failed: () -> Unit) {
        uiState.value.let { state ->
            if (state.email.isEmpty() || state.password.isEmpty()) {
                setIsError(true)
                return
            }
            setIsProcess(true)
            launchBack {
                doLogin(state, invoke, failed)
            }
        }
    }

    private fun setIsError(@Suppress("SameParameterValue") it: Boolean) {
        _uiState.update { state ->
            state.copy(isErrorPressed = it)
        }
    }

    private fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    data class State(
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val carModule: String = "",
        val carNumber: String = "",
        val carColor: String = "",
        val password: String = "",
        val isLoginScreen: Boolean = false,
        val isProcess: Boolean = false,
        val isErrorPressed: Boolean = false,
    )

}