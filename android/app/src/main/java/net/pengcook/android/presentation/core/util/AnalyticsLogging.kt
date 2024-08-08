package net.pengcook.android.presentation.core.util

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

object AnalyticsLogging {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun viewLogEvent(screenName: String) {
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }
}
