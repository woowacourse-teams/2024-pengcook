package net.pengcook.android.data.datasource.usercontrol

import net.pengcook.android.data.model.usercontrol.BlockUserRequest
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

        override suspend fun fetchReportReasons(): Response<List<ReportReasonResponse>> = userControlService.fetchReportReasons()

        override suspend fun reportUser(
            accessToken: String,
            reportUserRequest: ReportUserRequest,
        ): Response<ReportResponse> = userControlService.reportUser(accessToken, reportUserRequest)
    }
