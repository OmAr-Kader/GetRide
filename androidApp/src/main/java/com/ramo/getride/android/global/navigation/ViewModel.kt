package com.ramo.getride.android.global.navigation

import androidx.lifecycle.ViewModel
import com.ramo.getride.di.Project
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    protected val project: Project
) : ViewModel(), kotlinx.coroutines.CoroutineScope {

    override val coroutineContext: kotlin.coroutines.CoroutineContext
        get() = kotlinx.coroutines.Dispatchers.Default + kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.CoroutineExceptionHandler { _, _ -> }

    fun launchBack(
        block: suspend kotlinx.coroutines.CoroutineScope.() -> Unit
    ): Job {

        return launch(kotlinx.coroutines.Dispatchers.Default, kotlinx.coroutines.CoroutineStart.DEFAULT, block)
    }

    override fun onCleared() {
        cancel()
        super.onCleared()
    }
}