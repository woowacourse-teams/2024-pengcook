package net.pengcook.android.data.datasource.usercontrol

import net.pengcook.android.data.model.usercontrol.BlockDataResponse
import net.pengcook.android.data.model.usercontrol.BlockUserRequest
import net.pengcook.android.data.model.usercontrol.FollowDataResponse
import net.pengcook.android.data.model.usercontrol.FollowUserRequest
import net.pengcook.android.data.model.usercontrol.ReportReasonResponse
import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.data.model.usercontrol.ReportUserRequest
import net.pengcook.android.data.remote.api.UserControlService
import net.pengcook.android.data.util.network.NetworkResponseHandler
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserControlDataSource
    @Inject
    constructor(
        private val userControlService: UserControlService,
    ) : NetworkResponseHandler(),
        UserControlDataSource {
        override suspend fun blockUser(
            accessToken: String,
            blockUserRequest: BlockUserRequest,
        ): Response<Unit> = userControlService.blockUser(accessToken, blockUserRequest)

        override suspend fun fetchReportReasons(): Response<List<ReportReasonResponse>> =
            userControlService.fetchReportReasons()

        override suspend fun reportUser(
            accessToken: String,
            reportUserRequest: ReportUserRequest,
        ): Response<ReportResponse> = userControlService.reportUser(accessToken, reportUserRequest)

        override suspend fun followUser(
            accessToken: String,
            followUserRequest: FollowUserRequest,
        ): Response<Unit> = userControlService.followUser(accessToken, followUserRequest)

        override suspend fun unfollowUser(
            accessToken: String,
            followUserRequest: FollowUserRequest,
        ): Response<Unit> = userControlService.unfollowUser(accessToken, followUserRequest)

        override suspend fun fetchFollowers(userId: Long): Response<FollowDataResponse> =
            userControlService.fetchFollowers(userId)

        override suspend fun fetchFollowings(userId: Long): Response<FollowDataResponse> =
            userControlService.fetchFollowings(userId)

        override suspend fun deleteFollower(
            accessToken: String,
            followUserRequest: FollowUserRequest,
        ): Response<Unit> = userControlService.deleteFollower(accessToken, followUserRequest)

        override suspend fun fetchBlockees(accessToken: String): Response<List<BlockDataResponse>> {
            return userControlService.fetchBlockees(
                accessToken = accessToken,
            )
        }

        override suspend fun unblockUser(
            accessToken: String,
            blockeeId: Long,
        ): Response<Unit> {
            return userControlService.unblockUser(accessToken, blockeeId)
        }
    }
