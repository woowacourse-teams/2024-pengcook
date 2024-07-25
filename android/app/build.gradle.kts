import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    val properties = Properties()
    properties.load(FileInputStream(rootProject.file("local.properties")))

    namespace = "net.pengcook.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.pengcook.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            properties.getProperty("google_web_client_id"),
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField(
                "String",
                "BASE_URL",
                properties.getProperty("base_url_release")
            )
        }

        debug {
            buildConfigField("String", "BASE_URL", properties.getProperty("base_url_dev"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.firebase.auth)
    val navVersion = "2.7.7"
    val pagingVersion = "3.3.0"
    val retrofitVersion = "2.11.0"
    val gsonVersion = "2.11.0"
    val coreKtx = "1.13.1"

    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Firebase Authentication
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}
