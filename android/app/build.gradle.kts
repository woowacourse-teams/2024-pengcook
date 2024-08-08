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
                properties.getProperty("base_url_release"),
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
    val navVersion = "2.7.7"
    val pagingVersion = "3.3.0"
    val retrofitVersion = "2.11.0"
    val okHttpVersion = "4.12.0"
    val gsonVersion = "2.11.0"
    val coreKtx = "1.13.1"
    val coroutineVersion = "1.8.1"

    implementation(libs.firebase.auth)
    implementation(libs.androidx.material3.android)
    implementation(libs.core)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
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
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")
    implementation("androidx.startup:startup-runtime:1.1.1")

    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("androidx.paging:paging-common:$pagingVersion")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Image Cropper
    implementation("commons-io:commons-io:2.4")
}
