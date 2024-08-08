package net.pengcook.android.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        DetailRecipeViewModelFactory(recipe, application.appModule.likeRepository)
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
    }

    private fun observeViewModel() {
        observeError()
        observeNavigationEvent()
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) { _ ->
            Toast.makeText(requireContext(), getString(R.string.detail_like_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeNavigationEvent() {
        viewModel.navigateToStepEvent.observe(viewLifecycleOwner) { navigationEvent ->
            val navigationAvailable = navigationEvent.getContentIfNotHandled() ?: return@observe
            if (navigationAvailable) {
                navigateToStep()
            }
        }

        viewModel.navigateToCommentEvent.observe(viewLifecycleOwner) { navigationEvent ->
            val navigationAvailable = navigationEvent.getContentIfNotHandled() ?: return@observe
            if (navigationAvailable) {
                navigateToComment()
            }
        }
    }

    private fun fetchRecipe() {
        binding.recipe = recipe
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun navigateToStep() {
        val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToRecipeStepFragment()
        findNavController().navigate(action)
    }

    private fun navigateToComment() {
        val action = DetailRecipeFragmentDirections.actionDetailRecipeFragmentToCommentFragment(recipeId = 1170)
        findNavController().navigate(action)
    }

    companion object {
        private const val RECIPE_KEY = "recipe_key"
    }
}
