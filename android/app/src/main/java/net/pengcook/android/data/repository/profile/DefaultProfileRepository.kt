package net.pengcook.android.data.repository.profile

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.profile.ProfileRemoteDataSource
import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.data.util.mapper.toRecipeForList
import net.pengcook.android.data.util.mapper.toUserProfile
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.presentation.core.model.RecipeForList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultProfileRepository
    @Inject
    constructor(
        private val sessionLocalDataSource: SessionLocalDataSource,
        private val profileRemoteDataSource: ProfileRemoteDataSource,
    ) : NetworkResponseHandler(),
        ProfileRepository {
        override suspend fun fetchUserInformation(userId: Long): Result<UserProfile> =
            runCatching {
                val response = profileRemoteDataSource.fetchUserInformation(userId)
                body(response, CODE_SUCCESSFUL).toUserProfile()
            }

        override suspend fun fetchMyUserInformation(): Result<UserProfile> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = profileRemoteDataSource.fetchMyUserInformation(accessToken)
                body(response, CODE_SUCCESSFUL).toUserProfile()
            }

        override suspend fun patchMyUserInformation(userProfile: UpdateProfileRequest): Result<Unit> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = profileRemoteDataSource.patchMyUserInformation(accessToken, userProfile)
                body(response, CODE_SUCCESSFUL)
            }

        override suspend fun fetchUserFeeds(
            userId: Long,
            pageNumber: Int,
            pageSize: Int,
        ): Result<List<RecipeForList>> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response =
                    profileRemoteDataSource.fetchUserFeeds(accessToken, userId, pageNumber, pageSize)
                body(response, CODE_SUCCESSFUL).map { recipeResponse -> recipeResponse.toRecipeForList() }
            }

        companion object {
            private const val CODE_SUCCESSFUL = 200
        }
    }
