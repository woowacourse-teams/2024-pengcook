package net.pengcook.android.data.repository.usercontrol

import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.presentation.core.model.ReportReason

interface UserControlRepository {
    suspend fun blockUser(blockeeId: Long): Result<Unit>

    suspend fun fetchReportReasons(): Result<List<ReportReason>>

    suspend fun reportUser(
        reporteeId: Long,
        reason: String,
        type: String,
        targetId: Long,
        details: String?,
    ): Result<ReportResponse>
}
