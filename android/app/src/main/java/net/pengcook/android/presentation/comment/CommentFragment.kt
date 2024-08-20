package net.pengcook.android.presentation.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCommentBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.comment.bottomsheet.CommentMenuBottomFragment
import net.pengcook.android.presentation.core.model.Comment

class CommentFragment : Fragment() {
    private var _binding: FragmentCommentBinding? = null
    private val binding: FragmentCommentBinding
        get() = _binding!!

    private val args by navArgs<CommentFragmentArgs>()
    private val recipeId: Long by lazy { args.recipeId }

    private val viewModel: CommentViewModel by viewModels {
        val appModule =
            (requireContext().applicationContext as DefaultPengcookApplication).appModule
        CommentViewModelFactory(
            recipeId = recipeId,
            commentRepository = appModule.commentRepository,
            userControlRepository = appModule.userControlRepository,
        )
    }
    private val adapter: CommentAdapter by lazy { CommentAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            FragmentCommentBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = viewLifecycleOwner
                vm = viewModel
            }
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.adapter = adapter
        observeViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModels() {
        observeCommentEmptyState()
        observeComments()
        observeQuitCommentEvent()
        observeShowCommentMenuEvent()
        observeCommentMenuEvents()

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            adapter.submitList(comments)
        }
    }

    private fun observeCommentEmptyState() {
        viewModel.isCommentEmpty.observe(viewLifecycleOwner) { event ->
            val isCommentEmpty = event.getContentIfNotHandled() ?: return@observe
            if (isCommentEmpty) {
                showToast(getString(R.string.comment_empty_list))
            }
        }
    }

    private fun observeComments() {
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            adapter.submitList(comments)
        }
    }

    private fun observeQuitCommentEvent() {
        viewModel.quitCommentEvent.observe(viewLifecycleOwner) { event ->
            val quitComment = event.getContentIfNotHandled() ?: return@observe
            if (quitComment) {
                navigateBack()
            }
        }
    }

    private fun observeShowCommentMenuEvent() {
        viewModel.showCommentMenuEvent.observe(viewLifecycleOwner) { event ->
            val comment = event.getContentIfNotHandled() ?: return@observe
            showCommentMenuBottomFragment(comment)
        }
    }

    private fun navigateBack() {
        // TODO : Add animation
        val navOptions =
            NavOptions
                .Builder()
                .setPopExitAnim(R.anim.slide_out_down)
                .build()

        findNavController().navigateUp()
    }

    private fun showCommentMenuBottomFragment(comment: Comment) {
        val commentMenuBottomFragment = CommentMenuBottomFragment.newInstance(comment, viewModel)
        commentMenuBottomFragment.show(parentFragmentManager, commentMenuBottomFragment.tag)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun observeCommentMenuEvents() {
        observeReportCommentEvent()
        observeBlockCommentEvent()
        observeDeleteCommentEvent()
    }

    private fun observeReportCommentEvent() {
        viewModel.reportCommentEvent.observe(viewLifecycleOwner) { event ->
            val comment = event.getContentIfNotHandled() ?: return@observe
            showToast(getString(R.string.comment_reported).format(comment.userName))
        }
    }

    private fun observeBlockCommentEvent() {
        viewModel.blockCommentEvent.observe(viewLifecycleOwner) { event ->
            val comment = event.getContentIfNotHandled() ?: return@observe
            viewModel.onBlockComment(comment)
        }
    }

    private fun observeDeleteCommentEvent() {
        viewModel.deleteCommentEvent.observe(viewLifecycleOwner) { event ->
            val comment = event.getContentIfNotHandled() ?: return@observe
            viewModel.onDeleteComment(comment.commentId)
            showToast(getString(R.string.comment_deleted))
        }
    }
}
