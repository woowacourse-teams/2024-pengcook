package net.pengcook.android.presentation.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.comment.CommentRepository
import net.pengcook.android.presentation.core.listener.ActionEventHandler
import net.pengcook.android.presentation.core.model.Comment
import net.pengcook.android.presentation.core.util.Event

class CommentViewModel(
    private val recipeId: Long,
    private val commentRepository: CommentRepository,
) : ViewModel(),
    ActionEventHandler,
    CommentEventHandler {
    private val _comments: MutableLiveData<List<Comment>> = MutableLiveData()
    val comments: LiveData<List<Comment>>
        get() = _comments

    val commentCount: LiveData<Int>
        get() =
            _comments.switchMap { comments ->
                MutableLiveData(comments.size)
            }

    val commentContent: MutableLiveData<String> = MutableLiveData()

    private val _isCommentEmpty: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isCommentEmpty: LiveData<Event<Boolean>>
        get() = _isCommentEmpty

    init {
        initializeComments()
    }

    override fun onAction() {
        /*val content = commentContent.value

        viewModelScope.launch {
            if (content != null) {
                commentRepository.postComment(
                    recipeId = recipeId,
                    message = content,
                )
            }
        }
*/
        viewModelScope.launch {
            postComment()
            commentContent.value = ""
        }
    }

    private fun initializeComments() {
        Log.d("crong", "initializeComments")
        viewModelScope.launch {
            val comments = fetchComments()
            if (comments.isEmpty()) {
                _isCommentEmpty.value = Event(true)
                return@launch
            }
            _comments.value = comments
            Log.d("crong", "comments: $comments")
            Log.d("crong", "comments size : ${comments.size}")
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

    override fun onMenuButtonClicked() {
        TODO("bottom dialog sheet fragment show")
    }
}
