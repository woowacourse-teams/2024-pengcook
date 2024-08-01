package net.pengcook.android.data.datasource.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.pengcook.android.data.model.auth.Authorization
import net.pengcook.android.domain.model.auth.Platform

class FakeTokenLocalDataSource : TokenLocalDataSource {
    private var authorization =
        Authorization(
            platformToken = null,
            accessToken = null,
            refreshToken = null,
            fcmToken = null,
            currentPlatform = null,
        )

    override val authorizationData: Flow<Authorization> = flow { emit(authorization) }

    override suspend fun updatePlatformToken(platformToken: String?) {
        authorization =
            authorization.copy(
                platformToken = platformToken,
            )
    }

    override suspend fun updateAccessToken(accessToken: String?) {
        authorization =
            authorization.copy(
                accessToken = accessToken,
            )
    }

    override suspend fun updateRefreshToken(refreshToken: String?) {
        authorization =
            authorization.copy(
                refreshToken = refreshToken,
            )
    }

    override suspend fun updateFcmToken(fcmToken: String?) {
        authorization =
            authorization.copy(
                fcmToken = fcmToken,
            )
    }

    override suspend fun updateCurrentPlatform(platform: Platform) {
        authorization =
            authorization.copy(
                currentPlatform = platform.platformName,
            )
    }

    override suspend fun clearAll() {
        authorization =
            authorization.copy(
                null,
                null,
                null,
                null,
                null,
            )
    }
}
