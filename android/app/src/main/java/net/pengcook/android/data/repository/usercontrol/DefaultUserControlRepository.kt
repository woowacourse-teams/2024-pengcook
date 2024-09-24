package net.pengcook.android.data.repository.usercontrol

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.usercontrol.UserControlDataSource
import net.pengcook.android.data.model.usercontrol.BlockUserRequest
import net.pengcook.android.data.model.usercontrol.ReportReasonResponse
import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.data.model.usercontrol.ReportUserRequest
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.presentation.core.model.ReportReason
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class
DefaultUserControlRepository@Inject
    constructor(
        private val sessionLocalDataSource: SessionLocalDataSource,
        private val userControlDataSource: UserControlDataSource,
    ) : NetworkResponseHandler(),
        UserControlRepository {
        override suspend fun blockUser(blockeeId: Long): Result<Unit> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val blockUserRequest =
                    BlockUserRequest(
                        blockeeId = blockeeId,
                    )
                val response = userControlDataSource.blockUser(accessToken, blockUserRequest)
                body(response, VALID_POST_CODE)
            }

        override suspend fun fetchReportReasons(): Result<List<ReportReason>> =
            runCatching {
                val response = userControlDataSource.fetchReportReasons()
                body(response, RESPONSE_CODE_SUCCESS).map { it.toReportReason() }
            }

        override suspend fun reportUser(
            reporteeId: Long,
            reason: String,
            type: String,
            targetId: Long,
            details: String?,
        ): Result<ReportResponse> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val reportUserRequest =
                    ReportUserRequest(
                        reporteeId = reporteeId,
                        reason = reason,
                        type = type,
                        targetId = targetId,
                        details = details,
                    )
                val response = userControlDataSource.reportUser(accessToken, reportUserRequest)
                body(response, VALID_POST_CODE)
            }

        private fun ReportReasonResponse.toReportReason() =
            ReportReason(
                reason = reason,
                message = message,
            )

        companion object {
            private const val VALID_POST_CODE = 201
            private const val RESPONSE_CODE_SUCCESS = 200
        }
    }
