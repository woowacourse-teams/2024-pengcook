package net.pengcook.android.data.datasource.auth

import kotlinx.coroutines.flow.Flow
import net.pengcook.android.data.model.auth.Authorization
import net.pengcook.android.data.model.auth.Platform

interface AuthorizationLocalDataSource {
    val authorizationData: Flow<Authorization>

    suspend fun updatePlatformToken(platformToken: String?)

    suspend fun updateAccessToken(accessToken: String?)

    suspend fun updateRefreshToken(refreshToken: String?)

    suspend fun updateFcmToken(fcmToken: String?)

    suspend fun updateCurrentPlatform(platform: Platform)

    suspend fun clearAll()
}