package net.pengcook.android.presentation

import net.pengcook.android.di.AppModule
import net.pengcook.android.di.DefaultAppModule

class DefaultPengcookApplication : PengcookApplication() {
    override val appModule: AppModule by lazy {
        DefaultAppModule(this)
    }
}
