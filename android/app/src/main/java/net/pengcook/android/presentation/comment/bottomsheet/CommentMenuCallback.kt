package net.pengcook.android.presentation.comment.bottomsheet

import net.pengcook.android.presentation.core.model.Comment
import net.pengcook.android.presentation.core.model.ReportReason

interface CommentMenuCallback {
    fun onReport(
        comment: Comment,
        reportReason: ReportReason,
    )

    fun onBlock(comment: Comment)

    fun onDelete(comment: Comment)
}
