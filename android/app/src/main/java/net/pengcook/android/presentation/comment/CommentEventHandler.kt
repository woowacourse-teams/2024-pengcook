package net.pengcook.android.presentation.comment

import net.pengcook.android.presentation.core.listener.ActionEventHandler
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.model.Comment

interface CommentEventHandler :
    ActionEventHandler,
    AppbarSingleActionEventListener {
    fun onMenuButtonClicked(comment: Comment)

    override fun onAction()

    override fun onNavigateBack()
}
