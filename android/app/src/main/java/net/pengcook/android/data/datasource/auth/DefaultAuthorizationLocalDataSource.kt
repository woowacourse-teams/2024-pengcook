package net.pengcook.android.data.datasource.auth

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import net.pengcook.android.data.model.auth.Authorization
import net.pengcook.android.data.model.auth.Platform

class DefaultAuthorizationLocalDataSource(
    private val dataStore: DataStore<Preferences>,
) : AuthorizationLocalDataSource {
    override val authorizationData: Flow<Authorization> =
        dataStore
            .data
            .catchException()
            .map(::mapUserPreferences)

    override suspend fun updatePlatformToken(platformToken: String?) {
        dataStore.modifyValue(KEY_PLATFORM_TOKEN, platformToken)
    }

    override suspend fun updateAccessToken(accessToken: String?) {
        dataStore.modifyValue(KEY_ACCESS_TOKEN, accessToken)
    }

    override suspend fun updateRefreshToken(refreshToken: String?) {
        dataStore.modifyValue(KEY_REFRESH_TOKEN, refreshToken)
    }

    override suspend fun updateFcmToken(fcmToken: String?) {
        dataStore.modifyValue(KEY_FCM_TOKEN, fcmToken)
    }

    override suspend fun updateCurrentPlatform(platform: Platform) {
        dataStore.modifyValue(KEY_CURRENT_PLATFORM, platform.platformName)
    }

    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private suspend fun DataStore<Preferences>.modifyValue(
        key: Preferences.Key<String>,
        value: String?,
    ) {
        edit { preferences ->
            if (value == null) {
                preferences.remove(key)
                return@edit
            }
            preferences[key] = value
        }
    }

    private fun Flow<Preferences>.catchException(): Flow<Preferences> =
        catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, EXCEPTION_ERROR_READING_EXCEPTION, exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    private fun mapUserPreferences(preferences: Preferences): Authorization {
        val platformToken = preferences[KEY_PLATFORM_TOKEN]
        val accessToken = preferences[KEY_ACCESS_TOKEN]
        val refreshToken = preferences[KEY_REFRESH_TOKEN]
        val fcmToken = preferences[KEY_FCM_TOKEN]
        val currentPlatform = preferences[KEY_CURRENT_PLATFORM] ?: Platform.NONE.platformName

        return Authorization(
            platformToken = platformToken,
            accessToken = accessToken,
            refreshToken = refreshToken,
            fcmToken = fcmToken,
            currentPlatform = currentPlatform,
        )
    }

    companion object {
        private const val TAG = "DefaultAuthorizationLocalDataSource"
        private const val EXCEPTION_ERROR_READING_EXCEPTION = "Error reading preferences."
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_PLATFORM_TOKEN = stringPreferencesKey("platform_token")
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_CURRENT_PLATFORM = stringPreferencesKey("current_platform")
    }
}
