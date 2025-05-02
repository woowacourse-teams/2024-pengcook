package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.usercontrol.BlockDataResponse
import net.pengcook.android.data.model.usercontrol.BlockUserRequest
import net.pengcook.android.data.model.usercontrol.FollowDataResponse
import net.pengcook.android.data.model.usercontrol.FollowUserRequest
import net.pengcook.android.data.model.usercontrol.ReportReasonResponse
import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.data.model.usercontrol.ReportUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserControlService {
    @GET("/user/report/reason")
    suspend fun fetchReportReasons(): Response<List<ReportReasonResponse>>

    @POST("/user/block")
    suspend fun blockUser(
        @Header("Authorization") accessToken: String,
        @Body blockUserRequest: BlockUserRequest,
    ): Response<Unit>

    @POST("/user/report")
    suspend fun reportUser(
        @Header("Authorization") accessToken: String,
        @Body reportUserRequest: ReportUserRequest,
    ): Response<ReportResponse>

    @POST("/user/follow")
    suspend fun followUser(
        @Header("Authorization") accessToken: String,
        @Body followUserRequest: FollowUserRequest,
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "/user/follow", hasBody = true)
    suspend fun unfollowUser(
        @Header("Authorization") accessToken: String,
        @Body followUserRequest: FollowUserRequest,
    ): Response<Unit>

    @GET("/user/{userId}/follower")
    suspend fun fetchFollowers(
        @Path("userId") userId: Long,
    ): Response<FollowDataResponse>

    @GET("/user/{userId}/following")
    suspend fun fetchFollowings(
        @Path("userId") userId: Long,
    ): Response<FollowDataResponse>

    @GET("/users/me/blockees")
    suspend fun fetchBlockees(
        @Header("Authorization") accessToken: String,
    ): Response<List<BlockDataResponse>>

    @DELETE("/users/me/blockees/{blockeeId}")
    suspend fun unblockUser(
        @Header("Authorization") accessToken: String,
        @Path("blockeeId") blockeeId: Long,
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "/user/follower", hasBody = true)
    suspend fun deleteFollower(
        @Header("Authorization") accessToken: String,
        @Body followUserRequest: FollowUserRequest,
    ): Response<Unit>
}
