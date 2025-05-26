package net.pengcook.android.data.repository.auth

import kotlinx.coroutines.flow.Flow
import net.pengcook.android.data.datasource.auth.FakeSessionLocalDataSource
import net.pengcook.android.data.model.auth.Session
import net.pengcook.android.domain.model.auth.Platform

class FakeSessionRepository(
    private val fakeTokenLocalDataSource: FakeSessionLocalDataSource,
) : SessionRepository {
    override val sessionData: Flow<Session> = fakeTokenLocalDataSource.sessionData

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
