package net.pengcook.android.data.datasource.usercontrol

import net.pengcook.android.data.model.usercontrol.BlockDataResponse
import net.pengcook.android.data.model.usercontrol.BlockUserRequest
import net.pengcook.android.data.model.usercontrol.FollowDataResponse
import net.pengcook.android.data.model.usercontrol.FollowUserRequest
import net.pengcook.android.data.model.usercontrol.ReportReasonResponse
import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.data.model.usercontrol.ReportUserRequest
import retrofit2.Response

interface UserControlDataSource {
    suspend fun blockUser(
        accessToken: String,
        blockUserRequest: BlockUserRequest,
    ): Response<Unit>

    suspend fun fetchReportReasons(): Response<List<ReportReasonResponse>>

    suspend fun reportUser(
        accessToken: String,
        reportUserRequest: ReportUserRequest,
    ): Response<ReportResponse>

    suspend fun followUser(
        accessToken: String,
        followUserRequest: FollowUserRequest,
    ): Response<Unit>

    suspend fun unfollowUser(
        accessToken: String,
        followUserRequest: FollowUserRequest,
    ): Response<Unit>

    // authorization header
    // more data, at least userId
    suspend fun fetchFollowers(
        userId: Long,
    ): Response<FollowDataResponse>

    suspend fun fetchFollowings(
        userId: Long,
    ): Response<FollowDataResponse>

    suspend fun deleteFollower(
        accessToken: String,
        followUserRequest: FollowUserRequest,
    ): Response<Unit>

    suspend fun fetchBlockees(
        accessToken: String,
    ): Response<List<BlockDataResponse>>

    suspend fun unblockUser(
        accessToken: String,
        blockeeId: Long,
    ): Response<Unit>
}
