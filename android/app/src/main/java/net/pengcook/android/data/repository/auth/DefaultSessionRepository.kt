package net.pengcook.android.data.repository.auth

import kotlinx.coroutines.flow.Flow
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.model.auth.Session
import net.pengcook.android.domain.model.auth.Platform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSessionRepository
    @Inject
    constructor(
        private val authorizationLocalDataSource: SessionLocalDataSource,
    ) : SessionRepository {
        override val sessionData: Flow<Session> =
            authorizationLocalDataSource.sessionData

        override suspend fun updatePlatformToken(platformToken: String?) {
            authorizationLocalDataSource.updatePlatformToken(platformToken)
        }

        override suspend fun updateAccessToken(accessToken: String?) {
            authorizationLocalDataSource.updateAccessToken(accessToken)
        }

        override suspend fun updateRefreshToken(refreshToken: String?) {
            authorizationLocalDataSource.updateRefreshToken(refreshToken)
        }

        override suspend fun updateFcmToken(fcmToken: String?) {
            authorizationLocalDataSource.updateFcmToken(fcmToken)
        }

        override suspend fun updateCurrentPlatform(platform: Platform) {
            authorizationLocalDataSource.updateCurrentPlatform(platform)
        }

        override suspend fun clearAll() {
            authorizationLocalDataSource.clearAll()
        }
    }
