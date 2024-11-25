plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlitics)
}

android {
    namespace = "org.satochip.seedkeeper"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.satochip.seedkeeper"
        minSdk = 24
        targetSdk = 34
        versionCode = 102 // if versionName is x.y.z, versionCode is 10000x+100y+z
        versionName = "0.1.2" // using semantic versioning x.y.z (0<=x,y,z<=99)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //satochip libraries
    implementation(files("libs/satochip-lib-0.2.3.jar"))
    implementation(files("libs/satochip-android-0.0.2.jar"))

    // libs dependencies
    implementation(libs.bitcoinj.core)
    implementation(libs.guava)

    //Type safe navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //Compose coil image
    implementation(libs.coil.gif)
    implementation(libs.coil.compose)

    //QR string to image converter
    implementation(libs.qrcode.kotlin.android)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}