plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    id("com.google.dagger.hilt.android") version "2.57" apply false
    alias(libs.plugins.kotlin.compose) apply false
}