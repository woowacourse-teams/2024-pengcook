package net.pengcook.android.presentation.core.util

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

object AnalyticsLogging {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        if (firebaseAnalytics == null) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        }
    }

    fun viewLogEvent(screenName: String) {
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }
}
