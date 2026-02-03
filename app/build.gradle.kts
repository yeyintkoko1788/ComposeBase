plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt.android)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
}

android {
    namespace = "com.yeyint.composebase"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.composebase"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    ndkVersion = "28.1.13356709"
    flavorDimensions += "environment"
    productFlavors {
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging" // Adds ".staging" to the package name
            //versionNameSuffix = "-staging"  // Optional: Adds "-staging" to the version name
            buildConfigField("boolean", "IS_PRODUCTION", "false")
            resValue("string", "app_name", "Base Staging") // Optional: Custom app name
        }
        create("production") {
            dimension = "environment"
            // No suffix for production (uses the default package name)
            buildConfigField("boolean", "IS_PRODUCTION", "true")
            resValue("string", "app_name", "Base Production") // Optional: Custom app name
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.android.lottie.compose)
    implementation(libs.compose.material.icons)
    implementation(libs.coil.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.android.navigation.compose)

    debugImplementation(libs.github.chucker.debug)

    //retrofit
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.adapter.rxjava2)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.simplexml)
    implementation(libs.jsoup)

    //flow
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.retrofit2.kotlin.coroutines.adapter)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.resultat)

    //room
    implementation(libs.room.rumtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    //hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}