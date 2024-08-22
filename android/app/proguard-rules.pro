# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep the model classes
-keep class net.pengcook.android.data.model.**{ *; }

# -keep class net.pengcook.android.presentation.** { *; } # Fragment 클래스가 있는 패키지
# -keep class net.pengcook.android.presentation.main.MainActivity { *; } # MainActivity 클래스
# -keep class net.pengcook.android.presentation.BindingAdaptersKt { *; } # BindingAdaptersKt 클래스

-keep class net.pengcook.android.presentation.core.model.** { *; }
#-keep class net.pengcook.android.presentation.core.model.User { *; }
#-keep class net.pengcook.android.presentation.core.model.Recipe { *; }
# -keep class net.pengcook.android.presentation.main.** { *; }
# -keep class net.pengcook.android.presentation.onboarding.** { *; }
# -keep class net.pengcook.android.presentation.home.** { *; }

# Keep resource IDs
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep all Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Suppress warnings for Firebase packages
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**

# Keep all Google Cloud Platform classes
-keep class com.google.cloud.** { *; }

# Suppress warnings for Google Cloud Platform packages
-dontwarn com.google.cloud.**
-dontwarn com.google.api.**
-dontwarn com.google.auth.**
-dontwarn com.google.protobuf.**

# Keep Google authentication classes
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }

# Suppress warnings for Google authentication packages
-dontwarn com.google.android.gms.auth.**
-dontwarn com.google.android.gms.common.**

# Keep Retrofit classes
-keep class retrofit2.** { *; }

# Suppress warnings for Retrofit packages
-dontwarn retrofit2.**

# Keep OkHttp classes
-keep class okhttp3.** { *; }

# Suppress warnings for OkHttp packages
-dontwarn okhttp3.**

# Keep Gson classes
-keep class com.google.gson.** { *; }

# Keep Glide classes
-keep class com.bumptech.glide.** { *; }
