package net.pengcook.android.presentation

import android.app.Application
import net.pengcook.android.di.AppModule

abstract class PengcookApplication : Application() {
    abstract val appModule: AppModule
}
