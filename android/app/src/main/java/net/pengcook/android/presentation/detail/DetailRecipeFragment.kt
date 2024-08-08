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
import net.pengcook.android.data.datasource.like.DefaultLikeRemoteDataSource
import net.pengcook.android.data.remote.api.LikeService
import net.pengcook.android.data.repository.like.DefaultLikeRepository
import net.pengcook.android.data.util.network.RetrofitClient
import net.pengcook.android.databinding.FragmentDetailRecipeBinding
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.AnalyticsLogging

class DetailRecipeFragment : Fragment() {
    private val args: DetailRecipeFragmentArgs by navArgs()
    private val binding by lazy { FragmentDetailRecipeBinding.inflate(layoutInflater) }
    private val recipe: Recipe by lazy { args.recipe }

    private val likeService = RetrofitClient.service(LikeService::class.java)
    private val likeRemoteDataSource = DefaultLikeRemoteDataSource(likeService)
    private val likeRepository = DefaultLikeRepository(likeRemoteDataSource)

    private val viewModel: DetailRecipeViewModel by viewModels {
        DetailRecipeViewModelFactory(recipe, likeRepository)
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
        AnalyticsLogging.viewLogEvent("DetailRecipe")
        fetchRecipe()
        observeNavigationEvent()
        observeViewModel()
    }

    private fun observeNavigationEvent() {
        viewModel.navigateToStepEvent.observe(viewLifecycleOwner) { navigationEvent ->
            val navigationAvailable = navigationEvent.getContentIfNotHandled() ?: return@observe
            if (navigationAvailable) {
                navigateToStep()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
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

    companion object {
        private const val RECIPE_KEY = "recipe_key"
    }
}
