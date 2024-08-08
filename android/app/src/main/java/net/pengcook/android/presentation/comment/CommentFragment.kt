package net.pengcook.android.presentation.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCommentBinding
import net.pengcook.android.presentation.DefaultPengcookApplication

class CommentFragment : Fragment() {
    private var _binding: FragmentCommentBinding? = null
    private val binding: FragmentCommentBinding
        get() = _binding!!

    private val args by navArgs<CommentFragmentArgs>()
    private val recipeId: Long by lazy { args.recipeId }

    private val viewModel: CommentViewModel by viewModels {
        val appModule =
            (requireContext().applicationContext as DefaultPengcookApplication).appModule
        /*CommentViewModelFactory(
            recipeId = 1L,
            commentRepository =
                FakeCommentRepository(
                    dataSource = FakeCommentDataSource(),
                ),
            DefaultSessionLocalDataSource(requireContext().dataStore),
        )*/

        CommentViewModelFactory(
            recipeId = recipeId,
            commentRepository = appModule.commentRepository,
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
        setUpComments()
        observeViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModels() {
        observeCommentEmptyState()
        observeComments()
    }

    private fun setUpComments() {
        val comments = viewModel.comments
        adapter.submitList(comments.value)
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
