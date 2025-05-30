package net.pengcook.android.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentDetailRecipeBinding
import net.pengcook.android.presentation.core.model.RecipeForItem
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.detail.dialog.ReportDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class DetailRecipeFragment : Fragment() {
    private val args: DetailRecipeFragmentArgs by navArgs()
    private val binding by lazy { FragmentDetailRecipeBinding.inflate(layoutInflater) }
    private val recipeId: Long by lazy { args.recipeId }

    @Inject
    lateinit var provideFactory: DetailRecipeViewModelFactory

    private val viewModel: DetailRecipeViewModel by viewModels {
        DetailRecipeViewModel.provideFactory(provideFactory, recipeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("DetailRecipe")
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        observeViewModel()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRecipe()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showPopupMenu(
        view: View,
        recipe: RecipeForItem,
    ) {
        val popupMenu = PopupMenu(requireContext(), view)
        val menuRes =
            if (recipe.mine) {
                R.menu.menu_detail_mine
            } else {
                R.menu.menu_detail_other
            }
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_block -> {
                    viewModel.blockUser()
                    true
                }

                R.id.action_delete -> {
                    viewModel.deleteRecipe()
                    true
                }

                R.id.action_edit -> {
                    val action =
                        DetailRecipeFragmentDirections.actionDetailRecipeFragmentToEditRecipeFragment(recipeId)
                    findNavController().navigate(action)
                    true
                }

                R.id.action_report -> {
                    // Handle report action
                    val dialog = ReportDialogFragment.newInstance(recipe)
                    dialog.show(parentFragmentManager, "ReportReasonDialog")
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun observeViewModel() {
        observeNavigationEvent()
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.uiState = it
        }
    }

    private fun observeNavigationEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event?.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is DetailRecipeUiEvent.NavigateToStep -> {
                    navigateToStep()
                }

                is DetailRecipeUiEvent.NavigateToComment -> {
                    navigateToComment()
                }

                is DetailRecipeUiEvent.NavigateBackWithDelete -> {
                    navigateBackWithDelete()
                }

                is DetailRecipeUiEvent.NavigateBackWithBlock -> {
                    navigateBackWithBlock()
                }

                is DetailRecipeUiEvent.NavigateBack -> {
                    findNavController().navigateUp()
                }

                is DetailRecipeUiEvent.DeletionError -> {
                    Toast
                        .makeText(
                            requireContext(),
                            getString(R.string.detail_error),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
                is DetailRecipeUiEvent.LoadRecipeFailure -> {
                    Toast
                        .makeText(
                            requireContext(),
                            getString(R.string.detail_error),
                            Toast.LENGTH_SHORT,
                        ).show()
                }

                is DetailRecipeUiEvent.OpenMenu -> {
                    val recipe = newEvent.recipe
                    showPopupMenu(binding.ivMenu, recipe)
                }
            }
        }
    }

    private fun navigateBackWithBlock() {
        Toast.makeText(requireContext(), "Recipe deleted", Toast.LENGTH_SHORT).show()
        val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateBackWithDelete() {
        Toast.makeText(requireContext(), "Recipe deleted", Toast.LENGTH_SHORT).show()
        val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun fetchRecipe() {
//        binding.recipe = recipe
    }

    private fun navigateToStep() {
        val action =
            DetailRecipeFragmentDirections.actionDetailRecipeFragmentToRecipeStepFragment(recipeId = recipeId)
        findNavController().navigate(action)
    }

    private fun navigateToComment() {
        val action =
            DetailRecipeFragmentDirections.actionDetailRecipeFragmentToCommentFragment(recipeId = recipeId)
        findNavController().navigate(action)
    }
}
