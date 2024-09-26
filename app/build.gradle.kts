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
        versionCode = 1
        versionName = "1.0"

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
    implementation(files("libs/satochip-lib-0.2.0.jar"))
    implementation(files("libs/satochip-android-0.0.2.jar"))

    // libs dependencies
    implementation("org.bitcoinj:bitcoinj-core:0.16.2")
    implementation("com.google.guava:guava:32.1.2-jre")

    //Type safe navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //Compose coil image
    implementation("io.coil-kt:coil-gif:2.0.0-rc02")
    implementation("io.coil-kt:coil-compose:2.0.0-rc02")

    //QR string to image converter
    implementation("io.github.g0dkar:qrcode-kotlin-android:3.3.0")

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}