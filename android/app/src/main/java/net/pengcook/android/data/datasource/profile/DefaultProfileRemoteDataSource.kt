package net.pengcook.android.data.datasource.profile

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.data.model.profile.UpdateProfileResponse
import net.pengcook.android.data.model.profile.UserProfileResponse
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.remote.api.ProfileService
import retrofit2.Response

class DefaultProfileRemoteDataSource(
    private val profileService: ProfileService,
    private val feedService: FeedService,
) : ProfileRemoteDataSource {
    override suspend fun fetchUserInformation(userId: Long): Response<UserProfileResponse> {
        return profileService.fetchUserInformation(userId)
    }

    override suspend fun fetchMyUserInformation(accessToken: String): Response<UserProfileResponse> {
        return profileService.fetchMyUserInformation(accessToken)
    }

    override suspend fun patchMyUserInformation(
        accessToken: String,
        userProfile: UpdateProfileRequest,
    ): Response<UpdateProfileResponse> {
        return profileService.patchMyUserInformation(accessToken, userProfile)
    }

    override suspend fun fetchUserFeeds(
        accessToken: String,
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Response<List<FeedItemResponse>> {
        return feedService.fetchRecipes(
            accessToken = accessToken,
            pageNumber = pageNumber,
            pageSize = pageSize,
            category = null,
            keyword = null,
            userId = userId,
        )
    }
}
