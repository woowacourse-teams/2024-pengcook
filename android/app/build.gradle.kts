import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")

    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")

    id("com.google.dagger.hilt.android")
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
        versionCode = 18
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            properties.getProperty("google_web_client_id"),
        )
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField(
                "String",
                "BASE_URL",
                properties.getProperty("base_url_release"),
            )
        }

        debug {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro",
//            )
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    val navVersion = "2.7.7"
    val pagingVersion = "3.3.0"
    val gsonVersion = "2.11.0"
    val coreKtx = "1.13.1"
    val coroutineVersion = "1.8.1"
    val roomVersion = "2.6.1"

    implementation(libs.firebase.auth)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Kotlin
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Glide
    implementation(libs.glide)

    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation(libs.converter.gson)
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Firebase Authentication
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Indicator animation open source
    implementation("com.tbuonomo:dotsindicator:5.0")

    // Preferences Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // splash
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.startup:startup-runtime:1.2.0")

    // room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("androidx.paging:paging-common-ktx:$pagingVersion")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Image Cropper
    implementation("commons-io:commons-io:2.4")

    // hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // compose
    implementation("androidx.compose.ui:ui:1.7.6")
    implementation("androidx.compose.material:material:1.7.6")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // ktlint for jetpack compose
    ktlint("com.pinterest:ktlint:0.50.0")
}
