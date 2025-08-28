plugins {
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.compose.compiler)  apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}
