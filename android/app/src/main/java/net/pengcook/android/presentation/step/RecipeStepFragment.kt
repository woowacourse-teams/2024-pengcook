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
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.databinding.FragmentRecipeStepBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import javax.inject.Inject

@AndroidEntryPoint
class RecipeStepFragment : Fragment() {
    private val args by navArgs<RecipeStepFragmentArgs>()
    private val recipeId: Long by lazy { args.recipeId }

    @Inject
    lateinit var viewModelFactory: RecipeStepViewModelFactory

    private val viewModel: RecipeStepViewModel by viewModels {
        RecipeStepViewModel.provideFactory(viewModelFactory, recipeId)
    }
    private var _binding: FragmentRecipeStepBinding? = null
    val binding: FragmentRecipeStepBinding
        get() = _binding!!

    private val recipeStepPagerRecyclerAdapter: RecipeStepPagerRecyclerAdapter by lazy {
        RecipeStepPagerRecyclerAdapter(
            viewPager2 = binding.vpStepRecipe,
            fragmentManager = childFragmentManager,
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
            // ViewPager 설정 후 위치 복원
            if (savedInstanceState != null) {
                val position = savedInstanceState.getInt("current_step_position", 0)
                setCurrentItem(position, false)
            } else {
                // ViewModel에서 위치 복원
                setCurrentItem(viewModel.currentPosition, false)
            }
        }
        observeViewModel()

        binding.dotsIndicator.attachTo(binding.vpStepRecipe)

        // 페이지 변경 시 ViewModel에 위치 저장
        binding.vpStepRecipe.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.currentPosition = position
                }
            },
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("current_step_position", binding.vpStepRecipe.currentItem)
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
            // 데이터 로드 후 위치 복원
            val position = viewModel.currentPosition
            binding.vpStepRecipe.setCurrentItem(position, false)
        }
    }
}
