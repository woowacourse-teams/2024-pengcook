package net.pengcook.android.presentation.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.comment.CommentRepository

class CommentViewModelFactory(
    private val recipeId: Long,
    private val commentRepository: CommentRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            return CommentViewModel(
                recipeId = recipeId,
                commentRepository = commentRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
