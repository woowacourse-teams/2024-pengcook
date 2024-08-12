package net.pengcook.android.presentation.comment.bottomsheet

import net.pengcook.android.presentation.core.model.Comment

interface CommentMenuCallback {
    fun onReport(comment: Comment)

    fun onBlock(comment: Comment)

    fun onDelete(comment: Comment)
}
