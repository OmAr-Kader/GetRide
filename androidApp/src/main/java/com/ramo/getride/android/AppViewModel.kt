package com.ramo.getride.android

import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.android.global.navigation.Screen
import com.ramo.getride.android.global.navigation.replace
import com.ramo.getride.android.global.navigation.valueOf
import com.ramo.getride.android.global.navigation.values
import com.ramo.getride.data.model.PreferenceData
import com.ramo.getride.data.model.UserPref
import com.ramo.getride.data.supaBase.SessionStatusData
import com.ramo.getride.data.supaBase.fetchSupaBaseUser
import com.ramo.getride.data.supaBase.userInfo
import com.ramo.getride.di.Project
import com.ramo.getride.global.base.PREF_NAME
import com.ramo.getride.global.base.PREF_PROFILE_IMAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(project: Project) : BaseViewModel(project) {

    @Suppress("PropertyName")
    val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private var prefsJob: kotlinx.coroutines.Job? = null

    private fun inti(invoke: List<PreferenceData>.() -> Unit) {
        prefsJob?.cancel()
        prefsJob = launchBack {
            project.pref.prefs {
                _uiState.update { state ->
                    state.copy(preferences = it)
                }
                invoke(it)
            }
        }
    }

    fun findUser(invoke: (UserPref?) -> Unit) {
        launchBack {
            findPrefString(PREF_NAME) { name ->
                findPrefString(PREF_PROFILE_IMAGE) { profileImage ->
                    launchBack {
                        userInfo()?.copy(name = name ?: "", profilePicture = profileImage ?: "")?.let {
                            _uiState.update { state ->
                                state.copy(userPref = it)
                            }
                            invoke(it)
                        } ?: invoke(null)
                    }
                }
            }
        }
    }

    fun findUserLive(invoke: (UserPref?) -> Unit) {
        launchBack {
            fetchSupaBaseUser { userBase, status ->
                if (userBase != null) {
                    findPrefString(PREF_NAME) { name ->
                        findPrefString(PREF_PROFILE_IMAGE) { profileImage ->
                            userBase.copy(name = name ?: "", profilePicture = profileImage ?: "").let {
                                _uiState.update { state ->
                                    state.copy(userPref = it, sessionStatus = status)
                                }
                                invoke(it)
                            }
                        }
                    }
                } else {
                     if (status == SessionStatusData.NotAuthenticated) {
                         invoke(null)
                     }
                    _uiState.update { state ->
                        state.copy(sessionStatus = status)
                    }
                }
            }
        }
    }

    fun findPrefString(
        key: String,
        value: (it: String?) -> Unit,
    ) {
        if (uiState.value.preferences.isEmpty()) {
            inti {
                value(this@inti.find { it1 -> it1.keyString == key }?.value)
            }
        } else {
            value(uiState.value.preferences.find { it.keyString == key }?.value)
        }
    }

    inline fun <reified T : Screen> findArg() = uiState.value.args.valueOf<T>()

    suspend inline fun <reified T : Screen> writeArguments(screen: T) = kotlinx.coroutines.coroutineScope {
        _uiState.update { state ->
            state.copy(args = state.args.toMutableList().replace(screen), dummy = state.dummy + 1)
        }
    }

    data class State(
        val isProcess: Boolean = false,
        val userPref: UserPref = UserPref(),
        val sessionStatus: SessionStatusData = SessionStatusData.LoadingFromStorage,
        val args: List<Screen> = values(),
        val preferences: List<PreferenceData> = listOf(),
        val dummy: Int = 0,
    )
}