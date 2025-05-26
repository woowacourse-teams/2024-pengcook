package net.pengcook.android.data.repository.auth

import kotlinx.coroutines.flow.Flow
import net.pengcook.android.data.model.auth.Session
import net.pengcook.android.domain.model.auth.Platform

interface SessionRepository {
    val sessionData: Flow<Session>

    suspend fun updatePlatformToken(platformToken: String?)

    suspend fun updateAccessToken(accessToken: String?)

    suspend fun updateRefreshToken(refreshToken: String?)

    suspend fun updateFcmToken(fcmToken: String?)

    suspend fun updateCurrentPlatform(platform: Platform)

    suspend fun clearAll()
}
