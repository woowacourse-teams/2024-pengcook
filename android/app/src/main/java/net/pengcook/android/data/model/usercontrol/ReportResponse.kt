package net.pengcook.android.data.model.usercontrol

data class ReportResponse(
    val reportId: Long,
    val reporterId: Long,
    val reporteeId: Long,
    val reason: String,
    val type: String,
    val targetId: Long,
    val details: String?,
    val createdAt: String,
)
