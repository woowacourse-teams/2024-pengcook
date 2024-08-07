package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.data.model.profile.UpdateProfileResponse
import net.pengcook.android.data.model.profile.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {
    @GET("/user/{userId}")
    suspend fun fetchUserInformation(
        @Path("userId") userId: Long,
    ): Response<UserProfileResponse>

    @GET("/user/me")
    suspend fun fetchMyUserInformation(
        @Header("Authorization") accessToken: String,
    ): Response<UserProfileResponse>

    @PATCH("/user/me")
    suspend fun patchMyUserInformation(
        @Header("Authorization") accessToken: String,
        @Body userProfile: UpdateProfileRequest,
    ): Response<UpdateProfileResponse>

    @GET("/search")
    suspend fun fetchUserFeeds(
        @Query("userId") userId: Long,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<List<FeedItemResponse>>
}
