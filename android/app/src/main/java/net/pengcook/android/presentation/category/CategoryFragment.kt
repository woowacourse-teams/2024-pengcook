package net.pengcook.android.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCategoryBinding
import net.pengcook.android.presentation.core.style.GridSpacingItemDecoration
import net.pengcook.android.presentation.core.util.AnalyticsLogging

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding: FragmentCategoryBinding
        get() = _binding!!
    private val viewModel: CategoryViewModel by viewModels()
    private val adapter: CategoryAdapter by lazy { CategoryAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("Category")
        binding.adapter = adapter
        setUpCategories()
        observeEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeEvents() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val uiEvent = event.getContentIfNotHandled() ?: return@observe
            when (uiEvent) {
                is CategoryUiEvent.NavigateToList -> {
                    val action =
                        CategoryFragmentDirections.actionCategoryFragmentToCategoryFeedListFragment(
                            uiEvent.categoryCode,
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun setUpCategories() {
        val categories = categories()
        adapter.submitList(categories)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing_category)
        binding.rvCategory.addItemDecoration(GridSpacingItemDecoration(3, spacingInPixels))
    }

    private fun categories() =
        listOf(
            Category(
                id = 100,
                title = "흑백요리사",
                imageUrl = "https://github.com/user-attachments/assets/81327a0a-715d-4c91-b570-43c3386d1866",
                code = "흑백요리사",
            ),
            Category(
                1,
                "한식",
                "https://www.90daykorean.com/wp-content/uploads/2015/04/bigstock-Bibimbap-On-A-Concrete-Backgro-449450285-1024x683.jpg",
                "Korean",
            ),
            Category(
                2,
                "일식",
                "https://railrocker.com/playground/wp-content/uploads/2020/02/Onigiri-traditional-Japanese-Rice-Balls.jpg",
                "Japanese",
            ),
            Category(
                3,
                "중식",
                "https://image.yes24.com/images/chyes24/0/a/3/7/0a379fd46d1e404ed63adc3beee52b33.jpg",
                "Chinese",
            ),
        )
}
