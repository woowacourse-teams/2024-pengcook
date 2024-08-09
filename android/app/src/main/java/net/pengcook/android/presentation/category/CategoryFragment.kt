package net.pengcook.android.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCategoryBinding
import net.pengcook.android.presentation.core.style.GridSpacingItemDecoration
import net.pengcook.android.presentation.core.util.AnalyticsLogging

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
                1,
                "Belgian",
                "https://www.starttravel.co.uk/media/images/84310b25-c298-444e-8695-5ee4830e1e79.png",
                "Belgian",
            ),
            Category(
                2,
                "French",
                "https://admin.expatica.com/fr/wp-content/uploads/sites/5/2023/11/french-food-1536x1024.jpg",
                "French",
            ),
            Category(
                3,
                "German",
                "https://admin.expatica.com/de/wp-content/uploads/sites/6/2023/11/bratwurst-sauerkraut.jpg",
                "German",
            ),
            Category(
                4,
                "Indonesian",
                "https://tasteofbali.ro/wp-content/uploads/2023/02/asian_food_Taste_of_Bali-1024x682.jpg",
                "Indonesian",
            ),
            Category(
                5,
                "Seafood",
                "https://cdn11.bigcommerce.com/s-iz8lky8j1x/product_images/uploaded_images/istock-520490716.jpg",
                "Seafood",
            ),
            Category(
                6,
                "Japanese",
                "https://railrocker.com/playground/wp-content/uploads/2020/02/Onigiri-traditional-Japanese-Rice-Balls.jpg",
                "Japanese",
            ),
            Category(
                7,
                "Korean",
                "https://www.90daykorean.com/wp-content/uploads/2015/04/bigstock-Bibimbap-On-A-Concrete-Backgro-449450285-1024x683.jpg",
                "Korean",
            ),
        )
}
