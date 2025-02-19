package net.pengcook.android.data.datasource.profile

import net.pengcook.android.data.model.feed.item.FeedItemResponseForList
import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.data.model.profile.UpdateProfileResponse
import net.pengcook.android.data.model.profile.UserProfileResponse
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.remote.api.ProfileService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultProfileRemoteDataSource
    @Inject
    constructor(
        private val profileService: ProfileService,
        private val feedService: FeedService,
    ) : ProfileRemoteDataSource {
        override suspend fun fetchUserInformation(
            accessToken: String,
            userId: Long,
        ): Response<UserProfileResponse> = profileService.fetchUserInformation(accessToken, userId)

        override suspend fun fetchMyUserInformation(accessToken: String): Response<UserProfileResponse> =
            profileService.fetchMyUserInformation(accessToken)

        override suspend fun patchMyUserInformation(
            accessToken: String,
            userProfile: UpdateProfileRequest,
        ): Response<UpdateProfileResponse> = profileService.patchMyUserInformation(accessToken, userProfile)

        override suspend fun fetchUserFeeds(
            accessToken: String,
            userId: Long,
            pageNumber: Int,
            pageSize: Int,
        ): Response<List<FeedItemResponseForList>> =
            feedService.fetchRecipes2(
                accessToken = accessToken,
                accept = "application/vnd.pengcook.v1+json",
                pageNumber = pageNumber,
                pageSize = pageSize,
                category = null,
                keyword = null,
                userId = userId,
            )
    }
