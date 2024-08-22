package net.pengcook.android.presentation.comment.bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.usercontrol.UserControlRepository

class CommentMenuBottomViewModelFactory(
    private val userControlRepository: UserControlRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentMenuBottomViewModel::class.java)) {
            return CommentMenuBottomViewModel(
                userControlRepository = userControlRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
