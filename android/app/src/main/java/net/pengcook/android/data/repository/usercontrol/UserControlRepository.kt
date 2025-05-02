package net.pengcook.android.data.repository.usercontrol

import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.domain.model.usercontrol.BlockInfo
import net.pengcook.android.domain.model.usercontrol.FollowInfo
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

    suspend fun followUser(targetId: Long): Result<Unit>

    suspend fun unfollowUser(targetId: Long): Result<Unit>

    suspend fun fetchFollowers(userId: Long): Result<FollowInfo>

    suspend fun fetchFollowings(userId: Long): Result<FollowInfo>

    suspend fun fetchBlockees(): Result<List<BlockInfo>>

    suspend fun unblockUser(blockeeId: Long): Result<Unit>

    suspend fun deleteFollower(targetId: Long): Result<Unit>
}
