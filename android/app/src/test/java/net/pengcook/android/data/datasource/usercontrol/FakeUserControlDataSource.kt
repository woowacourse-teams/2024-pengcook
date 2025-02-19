package net.pengcook.android.data.datasource.usercontrol

import net.pengcook.android.data.model.usercontrol.BlockUserRequest
import net.pengcook.android.data.model.usercontrol.FollowUserRequest
import net.pengcook.android.data.model.usercontrol.ReportReasonResponse
import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.data.model.usercontrol.ReportUserRequest
import retrofit2.Response

class FakeUserControlDataSource : UserControlDataSource {
    override suspend fun blockUser(
        accessToken: String,
        blockUserRequest: BlockUserRequest,
    ): Response<Unit> = Response.success(Unit)

    override suspend fun fetchReportReasons(): Response<List<ReportReasonResponse>> =
        Response.success(
            listOf(
                ReportReasonResponse(
                    reason = "SPAM_CONTENT",
                    message = "Excessive posting or spamming",
                ),
                ReportReasonResponse(
                    reason = "INAPPROPRIATE_CONTENT",
                    message = "Contains inappropriate content",
                ),
            ),
        )

    override suspend fun reportUser(
        accessToken: String,
        reportUserRequest: ReportUserRequest,
    ): Response<ReportResponse> =
        Response.success(
            ReportResponse(
                reportId = 1,
                reporterId = 1,
                reporteeId = reportUserRequest.reporteeId,
                reason = reportUserRequest.reason,
                type = reportUserRequest.type,
                targetId = reportUserRequest.targetId,
                details = reportUserRequest.details,
                createdAt = System.currentTimeMillis().toString(),
            ),
        )

    override suspend fun followUser(
        accessToken: String,
        followUserRequest: FollowUserRequest,
    ): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun unfollowUser(
        accessToken: String,
        followUserRequest: FollowUserRequest,
    ): Response<Unit> {
        TODO("Not yet implemented")
    }
}
