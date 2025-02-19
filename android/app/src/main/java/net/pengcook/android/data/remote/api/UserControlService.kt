package net.pengcook.android.data.remote.api

import net.pengcook.android.data.model.usercontrol.BlockUserRequest
import net.pengcook.android.data.model.usercontrol.FollowUserRequest
import net.pengcook.android.data.model.usercontrol.ReportReasonResponse
import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.data.model.usercontrol.ReportUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST

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
}
