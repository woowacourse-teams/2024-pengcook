package net.pengcook.android.presentation

import dagger.hilt.android.HiltAndroidApp
import net.pengcook.android.di.AppModule
import net.pengcook.android.di.DefaultAppModule

@HiltAndroidApp
class DefaultPengcookApplication : PengcookApplication() {
    override val appModule: AppModule by lazy {
        DefaultAppModule(this)
    }
}
