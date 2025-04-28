package net.pengcook.android.data.repository.usercontrol

import kotlinx.coroutines.flow.first
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.usercontrol.UserControlDataSource
import net.pengcook.android.data.model.usercontrol.BlockDataResponse
import net.pengcook.android.data.model.usercontrol.BlockUserRequest
import net.pengcook.android.data.model.usercontrol.BlockeeResponse
import net.pengcook.android.data.model.usercontrol.BlockerResponse
import net.pengcook.android.data.model.usercontrol.FollowDataResponse
import net.pengcook.android.data.model.usercontrol.FollowUserRequest
import net.pengcook.android.data.model.usercontrol.FollowerInfoResponse
import net.pengcook.android.data.model.usercontrol.ReportReasonResponse
import net.pengcook.android.data.model.usercontrol.ReportResponse
import net.pengcook.android.data.model.usercontrol.ReportUserRequest
import net.pengcook.android.data.util.network.NetworkResponseHandler
import net.pengcook.android.domain.model.usercontrol.BlockInfo
import net.pengcook.android.domain.model.usercontrol.Blockee
import net.pengcook.android.domain.model.usercontrol.Blocker
import net.pengcook.android.domain.model.usercontrol.FollowInfo
import net.pengcook.android.domain.model.usercontrol.FollowUserInfo
import net.pengcook.android.presentation.core.model.ReportReason
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserControlRepository
    @Inject
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

        override suspend fun followUser(targetId: Long): Result<Unit> =
            kotlin.runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken
                        ?: throw RuntimeException()
                val followUserRequest =
                    FollowUserRequest(
                        targetId = targetId,
                    )
                val response = userControlDataSource.followUser(accessToken, followUserRequest)
                body(response, VALID_POST_CODE)
            }

        override suspend fun unfollowUser(targetId: Long): Result<Unit> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val followUserRequest =
                    FollowUserRequest(
                        targetId = targetId,
                    )
                val response = userControlDataSource.unfollowUser(accessToken, followUserRequest)
                body(response, RESPONSE_CODE_DELETE_SUCCESS)
            }

        override suspend fun fetchFollowers(userId: Long): Result<FollowInfo> =
            runCatching {
                val response = userControlDataSource.fetchFollowers(userId)
                body(response, RESPONSE_CODE_SUCCESS).toFollowInfo()
            }

        override suspend fun fetchFollowings(userId: Long): Result<FollowInfo> =
            runCatching {
                val response = userControlDataSource.fetchFollowings(userId)
                body(response, RESPONSE_CODE_SUCCESS).toFollowInfo()
            }

        override suspend fun deleteFollower(targetId: Long): Result<Unit> = runCatching {
            val accessToken = sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
            val followUserRequest = FollowUserRequest(targetId = targetId)
            val response = userControlDataSource.deleteFollower(accessToken, followUserRequest)
            body(response, RESPONSE_CODE_DELETE_SUCCESS)
        }

        override suspend fun fetchBlockees(): Result<List<BlockInfo>> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = userControlDataSource.fetchBlockees(accessToken)
                body(response, RESPONSE_CODE_SUCCESS).map { it.toBlockInfo() }
            }

        override suspend fun unblockUser(blockeeId: Long): Result<Unit> =
            runCatching {
                val accessToken =
                    sessionLocalDataSource.sessionData.first().accessToken ?: throw RuntimeException()
                val response = userControlDataSource.unblockUser(
                    accessToken = accessToken,
                    blockeeId = blockeeId,
                )
                body(response, RESPONSE_CODE_DELETE_SUCCESS)
            }

        private fun ReportReasonResponse.toReportReason() =
            ReportReason(
                reason = reason,
                message = message,
            )

        private fun FollowDataResponse.toFollowInfo() =
            FollowInfo(
                follows = this.follows.map { it.toFollowUserInfo() },
                followCount = this.followCount,
            )

        private fun FollowerInfoResponse.toFollowUserInfo() =
            FollowUserInfo(
                userId = this.userId,
                username = this.username,
                profileImage = this.image,
            )

        private fun BlockDataResponse.toBlockInfo() =
            BlockInfo(
                blocker = this.blocker.toBlocker(),
                blockee = this.blockee.toBlockee(),
            )

        private fun BlockerResponse.toBlocker() =
            Blocker(
                id = this.id,
                email = this.email,
                username = this.username,
                nickname = this.nickname,
                image = this.image,
                region = this.region,
            )

        private fun BlockeeResponse.toBlockee() =
            Blockee(
                id = this.id,
                email = this.email,
                username = this.username,
                nickname = this.nickname,
                image = this.image,
                region = this.region,
            )

        companion object {
            private const val VALID_POST_CODE = 201
            private const val RESPONSE_CODE_DELETE_SUCCESS = 204
            private const val RESPONSE_CODE_SUCCESS = 200
        }
    }
