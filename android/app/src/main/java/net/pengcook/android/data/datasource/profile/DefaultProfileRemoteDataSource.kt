package net.pengcook.android.data.datasource.profile

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.data.model.profile.UpdateProfileResponse
import net.pengcook.android.data.model.profile.UserProfileResponse
import net.pengcook.android.data.remote.api.ProfileService
import retrofit2.Response

class DefaultProfileRemoteDataSource(
    private val profileService: ProfileService,
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
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Response<List<FeedItemResponse>> {
        return profileService.fetchUserFeeds(userId, pageNumber, pageSize)
    }
}
