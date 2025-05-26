// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false

    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    // hilt
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}
