package com.ramo.getride.android

import android.app.Application
import com.ramo.getride.android.global.base.Theme
import com.ramo.getride.android.global.base.generateTheme
import com.ramo.getride.android.global.util.isDarkMode
import com.ramo.getride.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule(BuildConfig.DEBUG) + module {
                single<Theme> { generateTheme(isDarkMode = isDarkMode) }
                viewModel { AppViewModel(get()) }
                //single { HomeViewModel(get()) }
            })
        }
    }
}