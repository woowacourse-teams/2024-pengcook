package net.pengcook.android.data.datasource.usercontrol

import net.pengcook.android.data.model.usercontrol.BlockUserRequest
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
}
