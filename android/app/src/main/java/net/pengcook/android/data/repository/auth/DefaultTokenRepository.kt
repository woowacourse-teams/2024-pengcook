package net.pengcook.android.data.repository.auth

import kotlinx.coroutines.flow.Flow
import net.pengcook.android.data.datasource.auth.TokenLocalDataSource
import net.pengcook.android.data.model.auth.Authorization
import net.pengcook.android.domain.model.auth.Platform

class DefaultTokenRepository(
    private val authorizationLocalDataSource: TokenLocalDataSource,
) : TokenRepository {
    override val authorizationData: Flow<Authorization> =
        authorizationLocalDataSource.authorizationData

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
