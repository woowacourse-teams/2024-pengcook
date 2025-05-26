package net.pengcook.android.presentation.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.comment.CommentRepository
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.comment.bottomsheet.CommentMenuCallback
import net.pengcook.android.presentation.core.model.Comment
import net.pengcook.android.presentation.core.model.ReportReason
import net.pengcook.android.presentation.core.util.Event

class CommentViewModel
    @AssistedInject
    constructor(
        @Assisted private val recipeId: Long,
        private val commentRepository: CommentRepository,
        private val userControlRepository: UserControlRepository,
    ) : ViewModel(),
        CommentEventHandler,
        CommentMenuCallback {
        private val _comments: MutableLiveData<List<Comment>> = MutableLiveData()
        val comments: LiveData<List<Comment>>
            get() = _comments

        val commentCount: LiveData<Int>
            get() =
                _comments.switchMap { comments ->
                    MutableLiveData(comments.size)
                }

        private val blockedUserIds = MutableLiveData<List<Long>>()

        val commentContent: MutableLiveData<String> = MutableLiveData()

        private val _quitCommentEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
        val quitCommentEvent: LiveData<Event<Boolean>>
            get() = _quitCommentEvent

        private val _showCommentMenuEvent: MutableLiveData<Event<Comment>> = MutableLiveData()
        val showCommentMenuEvent: LiveData<Event<Comment>>
            get() = _showCommentMenuEvent

        private val _reportCommentEvent: MutableLiveData<Event<Comment>> = MutableLiveData()
        val reportCommentEvent: LiveData<Event<Comment>>
            get() = _reportCommentEvent

        private val _blockCommentEvent: MutableLiveData<Event<Comment>> = MutableLiveData()
        val blockCommentEvent: LiveData<Event<Comment>>
            get() = _blockCommentEvent

        private val _deleteCommentEvent: MutableLiveData<Event<Comment>> = MutableLiveData()
        val deleteCommentEvent: LiveData<Event<Comment>>
            get() = _deleteCommentEvent

        init {
            initializeComments()
        }

        override fun onAction() {
            viewModelScope.launch {
                postComment()
                commentContent.value = ""
            }
        }

        override fun onNavigateBack() {
            _quitCommentEvent.value = Event(true)
        }

        private fun initializeComments() {
            viewModelScope.launch {
                val comments = fetchComments()
                _comments.value = comments.filter { it.userId !in (blockedUserIds.value ?: emptyList()) }
            }
        }

        private suspend fun postComment() {
            if (commentContent.value.isNullOrEmpty()) {
                return
            }
            val result =
                commentRepository.postComment(
                    recipeId = recipeId,
                    message = commentContent.value!!,
                )

            if (result.isSuccess) {
                initializeComments()
            }
        }

        private suspend fun fetchComments(): List<Comment> {
            val result =
                commentRepository.fetchComments(
                    recipeId = recipeId,
                )

            return result.getOrNull() ?: emptyList()
        }

        override fun onMenuButtonClicked(comment: Comment) {
            _showCommentMenuEvent.value = Event(comment)
        }

        private fun onReportComment(
            comment: Comment,
            reportReason: ReportReason,
        ) {
            viewModelScope.launch {
                userControlRepository.reportUser(
                    reporteeId = comment.userId,
                    reason = reportReason.reason,
                    type = "COMMENT",
                    targetId = comment.commentId,
                    details = null,
                )
                val reloadedComments = fetchComments()
                _comments.value = reloadedComments
            }
        }

        fun onBlockComment(comment: Comment) {
            viewModelScope.launch {
                userControlRepository.blockUser(comment.userId)
                val reloadedComments = fetchComments()
                _comments.value = reloadedComments
            }
        }

        fun onDeleteComment(commentId: Long) {
            viewModelScope.launch {
                deleteComment(commentId)
                val reloadedComments = fetchComments()
                _comments.value = reloadedComments
            }
        }

        private suspend fun deleteComment(commentId: Long) {
            commentRepository.deleteComment(commentId)
        }

        override fun onReport(
            comment: Comment,
            reportReason: ReportReason,
        ) {
            _reportCommentEvent.value = Event(comment)
            onReportComment(comment, reportReason)
        }

        override fun onBlock(comment: Comment) {
            _blockCommentEvent.value = Event(comment)
        }

        override fun onDelete(comment: Comment) {
            _deleteCommentEvent.value = Event(comment)
        }

        companion object {
            fun provideFactory(
                assistedFactory: CommentViewModelFactory,
                recipeId: Long,
            ): ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return assistedFactory.create(recipeId) as T
                    }
                }
        }
    }
