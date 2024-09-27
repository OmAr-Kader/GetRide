plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.ramo.getride.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.ramo.getride.android"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    kotlin {
        sourceSets {
            all {
                languageSettings {
                    //optIn("kotlin.RequiresOptIn")
                    optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                }
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildToolsVersion = "35.0.0"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation)
    implementation(libs.play.services.location)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlin.reflect)
    implementation(libs.zoomable)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.coil)
    implementation(libs.coil.video)
    implementation(libs.flexible.bottomsheet.material3)

    api(libs.maps.compose)
    implementation(libs.maps.utils.ktx)

}

