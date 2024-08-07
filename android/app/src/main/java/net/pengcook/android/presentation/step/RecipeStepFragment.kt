package net.pengcook.android.presentation.step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import net.pengcook.android.data.datasource.feed.DefaultFeedRemoteDataSource
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.repository.feed.DefaultFeedRepository
import net.pengcook.android.data.util.network.RetrofitClient
import net.pengcook.android.databinding.FragmentRecipeStepBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging

class RecipeStepFragment : Fragment() {
    private val recipeId: Long = 1L
    private val viewModel: RecipeStepViewModel by viewModels {
        RecipeStepViewModelFactory(
            recipeId = recipeId,
            feedRepository =
                DefaultFeedRepository(
                    feedRemoteDataSource =
                        DefaultFeedRemoteDataSource(
                            RetrofitClient.service(
                                FeedService::class.java,
                            ),
                        ),
                ),
        )
    }

    private var _binding: FragmentRecipeStepBinding? = null
    val binding: FragmentRecipeStepBinding
        get() = _binding!!

    private val recipeStepPagerRecyclerAdapter: RecipeStepPagerRecyclerAdapter by lazy {
        RecipeStepPagerRecyclerAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecipeStepBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.viewLogEvent("RecipeStep")
        viewModel.fetchRecipeSteps()

        viewModel.recipeSteps.observe(viewLifecycleOwner) { recipeSteps ->
            recipeStepPagerRecyclerAdapter.updateList(recipeSteps)
        }

        binding.vpStepRecipe.apply {
            adapter = recipeStepPagerRecyclerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        binding.dotsIndicator.attachTo(binding.vpStepRecipe)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
