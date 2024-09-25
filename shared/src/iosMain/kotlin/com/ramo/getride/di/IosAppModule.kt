package com.ramo.getride.di

import org.koin.core.context.startKoin

@Suppress("unused")
suspend fun initKoin(isDebugMode: Boolean) {
    val module = appModuleSus(isDebugMode)
    kotlinx.coroutines.coroutineScope {
        startKoin {
            modules(module)
        }
        Unit
    }
}