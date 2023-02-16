plugins {
    // Android Application?
    id("com.android.application")

    // Support for kotlin in Android
    kotlin("android")

    // Safe Navigation
    id("androidx.navigation.safeargs")

    // keep at bottom
    kotlin("kapt")
}

android {
    namespace = "com.dzeio.chartsapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.dzeio.chartsapp"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(project(":library"))

    // Material Design
    implementation("com.google.android.material:material:1.8.0")

    // Navigation because I don't want to maintain basic transactions and shit
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
}
