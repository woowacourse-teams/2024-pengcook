// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
