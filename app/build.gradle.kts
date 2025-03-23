plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt) // Corrected plugin name and uncommented
}

android {
    namespace = "com.example.geminitest" // CORRECTED PACKAGE NAME
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.geminitest" // CORRECTED PACKAGE NAME
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8 // Reverted to Java 8 for compatibility
        targetCompatibility = JavaVersion.VERSION_1_8 // Reverted to Java 8
    }
    kotlinOptions {
        jvmTarget = "1.8" // Reverted to Java 8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()//CORRECT
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Room (for local database - preferred for this use case)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)  // Kotlin extensions for Room
    kapt(libs.androidx.room.compiler)

    // DataStore Preferences (alternative, simpler, but less structured than Room)
    implementation(libs.androidx.datastore.preferences)


    // Material Components for Android (REQUIRED for Theme.Material3...)
    implementation("com.google.android.material:material:1.12.0") // Use latest stable version!

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}