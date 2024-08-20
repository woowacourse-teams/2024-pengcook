package net.pengcook.android.presentation.detail

import ReportDialogFragment
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
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentDetailRecipeBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.AnalyticsLogging

class DetailRecipeFragment : Fragment() {
    private val args: DetailRecipeFragmentArgs by navArgs()
    private val binding by lazy { FragmentDetailRecipeBinding.inflate(layoutInflater) }
    private val recipe: Recipe by lazy { args.recipe }

    private val viewModel: DetailRecipeViewModel by viewModels {
        val application = (requireContext().applicationContext) as DefaultPengcookApplication
        DetailRecipeViewModelFactory(
            recipe = recipe,
            likeRepository = application.appModule.likeRepository,
            feedRepository = application.appModule.feedRepository,
            userControlRepository = application.appModule.userControlRepository,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("DetailRecipe")
        fetchRecipe()
        observeViewModel()
        setupMenu()
    }

    private fun setupMenu() {
        binding.ivMenu.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(view: View) {
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
        observeError()
        observeNavigationEvent()
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) { _ ->
            Toast
                .makeText(
                    requireContext(),
                    getString(R.string.detail_error),
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }

    private fun observeNavigationEvent() {
        viewModel.navigateToStepEvent.observe(viewLifecycleOwner) { navigationEvent ->
            val navigationAvailable = navigationEvent.getContentIfNotHandled() ?: return@observe
            if (navigationAvailable) {
                navigateToStep()
            }
        }

        viewModel.navigateBackEvent.observe(viewLifecycleOwner) { navigationEvent ->
            navigationEvent.getContentIfNotHandled() ?: return@observe
            findNavController().navigateUp()
        }

        viewModel.navigateToCommentEvent.observe(viewLifecycleOwner) { navigationEvent ->
            val navigationAvailable = navigationEvent.getContentIfNotHandled() ?: return@observe
            if (navigationAvailable) {
                navigateToComment()
            }
        }

        viewModel.navigateBackEvent.observe(viewLifecycleOwner) { navigationEvent ->
            navigationEvent.getContentIfNotHandled() ?: return@observe
            findNavController().navigateUp()
        }

        viewModel.navigateBackEvent.observe(viewLifecycleOwner) { navigationEvent ->
            navigationEvent.getContentIfNotHandled() ?: return@observe
            findNavController().navigateUp()
        }

        viewModel.navigateBackWithDeleteEvent.observe(viewLifecycleOwner) { navigationEvent ->
            navigationEvent.getContentIfNotHandled() ?: return@observe
            Toast.makeText(requireContext(), "Recipe deleted", Toast.LENGTH_SHORT).show()
            val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToHomeFragment()
            findNavController().navigate(action)
        }

        viewModel.navigateBackWithBlockEvent.observe(viewLifecycleOwner) { navigationEvent ->
            navigationEvent.getContentIfNotHandled() ?: return@observe
            Toast.makeText(requireContext(), "User blocked", Toast.LENGTH_SHORT).show()
            val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun fetchRecipe() {
        binding.recipe = recipe
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun navigateToStep() {
        val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToRecipeStepFragment(recipeId = recipe.recipeId)
        findNavController().navigate(action)
    }

    private fun navigateToComment() {
        val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToCommentFragment(recipeId = recipe.recipeId)
        findNavController().navigate(action)
    }

    companion object {
        private const val RECIPE_KEY = "recipe_key"
    }
}
