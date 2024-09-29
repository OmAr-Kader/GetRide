package com.ramo.getride.android.ui.user.sign

import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.data.model.PreferenceData
import com.ramo.getride.data.model.User
import com.ramo.getride.data.model.UserPref
import com.ramo.getride.data.supaBase.registerAuth
import com.ramo.getride.data.supaBase.signInAuth
import com.ramo.getride.data.supaBase.userInfo
import com.ramo.getride.di.Project
import com.ramo.getride.global.base.PREF_ID
import com.ramo.getride.global.base.PREF_NAME
import com.ramo.getride.global.base.PREF_PROFILE_IMAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(project: Project) : BaseViewModel(project) {

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

    fun toggleScreen() {
        _uiState.update { state ->
            state.copy(isLoginScreen = !state.isLoginScreen, isErrorPressed = false)
        }
    }

    fun createNewUser(invoke: () -> Unit, failed: () -> Unit) {
        uiState.value.let { state ->
            if (state.email.isEmpty() || state.password.isEmpty() || state.name.isEmpty() || state.email.isEmpty() || state.phone.isEmpty()) {
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
            project.user.addNewUser(
                User(
                    authId = userBase.authId,
                    name = state.name,
                    email = userBase.email,
                    phone = state.phone
                )
            )?.let { user ->
                project.pref.updatePref(
                    listOf(
                        PreferenceData(PREF_ID, user.id.toString()),
                        PreferenceData(PREF_NAME, user.name),
                        PreferenceData(PREF_PROFILE_IMAGE, user.profilePicture)
                    )
                ).also {
                    invoke()
                }
            } ?: kotlin.run {
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
                    project.user.getUserOnAuthId(it.authId)?.also { user ->
                        project.pref.updatePref(
                            listOf(
                                PreferenceData(PREF_ID, user.id.toString()),
                                PreferenceData(PREF_NAME, user.name),
                                PreferenceData(PREF_PROFILE_IMAGE, user.profilePicture)
                            )
                        ).also {
                            invoke()
                        }
                    } ?: kotlin.run {
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

    fun loginUser(invoke: () -> Unit, failed: () -> Unit) {
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

    fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    data class State(
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val password: String = "",
        val isLoginScreen: Boolean = false,
        val isProcess: Boolean = false,
        val isErrorPressed: Boolean = false,
    )

}