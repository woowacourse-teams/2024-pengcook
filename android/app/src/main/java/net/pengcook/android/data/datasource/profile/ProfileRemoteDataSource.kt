package net.pengcook.android.data.datasource.profile

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.data.model.profile.UpdateProfileResponse
import net.pengcook.android.data.model.profile.UserProfileResponse
import retrofit2.Response

interface ProfileRemoteDataSource {
    suspend fun fetchUserInformation(userId: Long): Response<UserProfileResponse>

    suspend fun fetchMyUserInformation(accessToken: String): Response<UserProfileResponse>

    suspend fun patchMyUserInformation(
        accessToken: String,
        userProfile: UpdateProfileRequest,
    ): Response<UpdateProfileResponse>

    suspend fun fetchUserFeeds(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Response<List<FeedItemResponse>>
}
