package net.pengcook.android.data.model.usercontrol

data class ReportUserRequest(
    val reporteeId: Long,
    val reason: String,
    val type: String,
    val targetId: Long,
    val details: String?,
)
