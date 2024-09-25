plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.realm)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }

    /*listOf(
        iosX64(), // For iOS simulators on Intel Macs
        iosArm64(), // For physical iOS devices
        iosSimulatorArm64() // For iOS simulators on Apple Silicon Macs
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }*/

    sourceSets {
        all {
            languageSettings {
                //optIn("kotlin.RequiresOptIn")
                optIn("io.github.jan.supabase.annotations.SupabaseExperimental")
                optIn("kotlinx.datetime.format.FormatStringsInDatetimeFormats")
            }
        }
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.koin.core)
            implementation(libs.kvault)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.realm.base)
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(project.dependencies.enforcedPlatform(libs.supabase.bom))
            implementation(libs.supabase.gotrue)
            implementation(libs.supabase.realtime)
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.auth)
            implementation(libs.supabase.auth.ui)
            implementation(libs.supabase.storage)
            implementation(libs.ktor.client.core)
            api(libs.logging)
            implementation(libs.stately.common)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.stately.isolate)
            implementation(libs.stately.iso.collections)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.ramo.getride"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildToolsVersion = "35.0.0"
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0,TimeUnit.SECONDS)
}
