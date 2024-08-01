package net.pengcook.android.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val AUTHORIZATION_PREFERENCES = "authorization_preferences"

val Context.dataStore by preferencesDataStore(
    name = AUTHORIZATION_PREFERENCES,
)
