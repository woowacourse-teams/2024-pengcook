package net.pengcook.android.presentation.mycomments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.pengcook.android.R
import net.pengcook.android.data.repository.comment.CommentRepository
import net.pengcook.android.presentation.core.model.MyComment
import net.pengcook.android.presentation.mycomments.components.MyCommentsScreen
import net.pengcook.android.ui.theme.PengCookTheme
import javax.inject.Inject

@AndroidEntryPoint
class MyCommentsFragment : Fragment() {
    @Inject
    lateinit var commentRepository: CommentRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            val comments = fetchComments()
            setContent {
                PengCookTheme {
                    MyCommentsScreen(
                        comments = comments,
                        navigateToDetail = { recipeId -> navigateToDetailRecipe(recipeId) },
                        navigationBack = { navigationBack() },
                    )
                }
            }
        }

    private fun fetchComments(): List<MyComment> =
        runBlocking {
            withContext(Dispatchers.IO) {
                val result = commentRepository.fetchMyComments()
                result.getOrNull() ?: emptyList()
            }
        }

    private fun navigationBack() {
        findNavController().navigate(R.id.action_myCommentFragment_to_settingFragment)
    }

    private fun navigateToDetailRecipe(recipeId: Long) {
        findNavController().navigate(MyCommentsFragmentDirections.actionMyCommentFragmentToDetailRecipeFragment(recipeId))
    }
}
