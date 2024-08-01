package net.pengcook.android.data.repository.auth

import kotlinx.coroutines.flow.Flow
import net.pengcook.android.data.datasource.auth.FakeTokenLocalDataSource
import net.pengcook.android.data.model.auth.Authorization
import net.pengcook.android.domain.model.auth.Platform

class FakeTokenRepository(
    private val fakeTokenLocalDataSource: FakeTokenLocalDataSource,
) : TokenRepository {
    override val authorizationData: Flow<Authorization> = fakeTokenLocalDataSource.authorizationData

    override suspend fun updatePlatformToken(platformToken: String?) {
        fakeTokenLocalDataSource.updatePlatformToken(platformToken)
    }

    override suspend fun updateAccessToken(accessToken: String?) {
        fakeTokenLocalDataSource.updateAccessToken(accessToken)
    }

    override suspend fun updateRefreshToken(refreshToken: String?) {
        fakeTokenLocalDataSource.updateRefreshToken(refreshToken)
    }

    override suspend fun updateFcmToken(fcmToken: String?) {
        fakeTokenLocalDataSource.updateFcmToken(fcmToken)
    }

    override suspend fun updateCurrentPlatform(platform: Platform) {
        fakeTokenLocalDataSource.updateCurrentPlatform(platform)
    }

    override suspend fun clearAll() {
        fakeTokenLocalDataSource.clearAll()
    }
}
