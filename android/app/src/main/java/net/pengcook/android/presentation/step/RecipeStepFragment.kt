package net.pengcook.android.presentation.step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import net.pengcook.android.databinding.FragmentRecipeStepBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.util.AnalyticsLogging

class RecipeStepFragment : Fragment() {
    private val args by navArgs<RecipeStepFragmentArgs>()
    private val recipeId: Long by lazy { args.recipeId }
    private val viewModel: RecipeStepViewModel by viewModels {
        val appModule =
            (requireContext().applicationContext as DefaultPengcookApplication).appModule
        RecipeStepViewModelFactory(
            recipeId = recipeId,
            feedRepository = appModule.feedRepository,
        )
    }

    private var _binding: FragmentRecipeStepBinding? = null
    val binding: FragmentRecipeStepBinding
        get() = _binding!!

    private val recipeStepPagerRecyclerAdapter: RecipeStepPagerRecyclerAdapter by lazy {
        RecipeStepPagerRecyclerAdapter(
            viewPager2 = binding.vpStepRecipe,
            fragmentManager = childFragmentManager
        )
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
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("RecipeStep")
        viewModel.fetchRecipeSteps()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.vpStepRecipe.apply {
            adapter = recipeStepPagerRecyclerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        observeViewModel()

        binding.dotsIndicator.attachTo(binding.vpStepRecipe)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        observeRecipeSteps()
        observeQuitEvent()
    }

    private fun observeQuitEvent() {
        viewModel.quitEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigateUp()
            }
        }
    }

    private fun observeRecipeSteps() {
        viewModel.recipeSteps.observe(viewLifecycleOwner) { recipeSteps ->
            recipeStepPagerRecyclerAdapter.updateList(recipeSteps)
        }
    }
}
