package net.pengcook.android.presentation.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.comment.CommentRepository
import net.pengcook.android.presentation.comment.bottomsheet.CommentMenuCallback
import net.pengcook.android.presentation.core.model.Comment
import net.pengcook.android.presentation.core.util.Event

class CommentViewModel(
    private val recipeId: Long,
    private val commentRepository: CommentRepository,
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

    private val _isCommentEmpty: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isCommentEmpty: LiveData<Event<Boolean>>
        get() = _isCommentEmpty

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
            if (comments.isEmpty()) {
                _isCommentEmpty.value = Event(true)
                return@launch
            }
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

    fun onBlockComment(comment: Comment) {
        blockedUserIds.value = blockedUserIds.value?.plus(comment.userId) ?: listOf(comment.userId)
        val commentsAfterBlock =
            _comments.value?.filter {
                it.userId !in (blockedUserIds.value ?: emptyList())
            } ?: emptyList()
        _comments.value = commentsAfterBlock
    }

    fun onDeleteComment(commentId: Long) {
        viewModelScope.launch {
            deleteComment(commentId)
        }
        initializeComments()
    }

    private suspend fun deleteComment(commentId: Long) {
        val result = commentRepository.deleteComment(commentId)
        if (result.isSuccess) {
            initializeComments()
        }
    }

    override fun onReport(comment: Comment) {
        _reportCommentEvent.value = Event(comment)
    }

    override fun onBlock(comment: Comment) {
        _blockCommentEvent.value = Event(comment)
    }

    override fun onDelete(comment: Comment) {
        _deleteCommentEvent.value = Event(comment)
    }
}