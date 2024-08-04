package net.pengcook.android.data.datasource.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.pengcook.android.data.model.auth.Session
import net.pengcook.android.domain.model.auth.Platform

class FakeSessionLocalDataSource : SessionLocalDataSource {
    private var session =
        Session(
            platformToken = null,
            accessToken = null,
            refreshToken = null,
            fcmToken = null,
            currentPlatform = null,
        )

    override val sessionData: Flow<Session> = flow { emit(session) }

    override suspend fun updatePlatformToken(platformToken: String?) {
        session =
            session.copy(
                platformToken = platformToken,
            )
    }

    override suspend fun updateAccessToken(accessToken: String?) {
        session =
            session.copy(
                accessToken = accessToken,
            )
    }

    override suspend fun updateRefreshToken(refreshToken: String?) {
        session =
            session.copy(
                refreshToken = refreshToken,
            )
    }

    override suspend fun updateFcmToken(fcmToken: String?) {
        session =
            session.copy(
                fcmToken = fcmToken,
            )
    }

    override suspend fun updateCurrentPlatform(platform: Platform) {
        session =
            session.copy(
                currentPlatform = platform.platformName,
            )
    }

    override suspend fun clearAll() {
        session =
            session.copy(
                null,
                null,
                null,
                null,
                null,
            )
    }
}
