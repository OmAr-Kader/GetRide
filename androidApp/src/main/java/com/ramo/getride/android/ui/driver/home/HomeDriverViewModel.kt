package com.ramo.getride.android.ui.driver.home

import com.ramo.getride.android.global.navigation.BaseViewModel
import com.ramo.getride.di.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeDriverViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

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