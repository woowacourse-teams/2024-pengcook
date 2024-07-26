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
                "Dessert",
                "https://flexible.img.hani.co.kr/flexible/normal/640/427/imgdb/original/2024/0522/20240522501170.jpg",
                "dessert",
            ),
            Category(
                2,
                "Chicken",
                "https://flexible.img.hani.co.kr/flexible/normal/640/427/imgdb/original/2024/0522/20240522501170.jpg",
                "chicken",
            ),
            Category(
                3,
                "Vegetarian",
                "https://flexible.img.hani.co.kr/flexible/normal/640/427/imgdb/original/2024/0522/20240522501170.jpg",
                "vegetarian",
            ),
            Category(
                4,
                "Miscellaneous",
                "https://flexible.img.hani.co.kr/flexible/normal/640/427/imgdb/original/2024/0522/20240522501170.jpg",
                "miscellaneous",
            ),
            Category(
                5,
                "Seafood",
                "https://flexible.img.hani.co.kr/flexible/normal/640/427/imgdb/original/2024/0522/20240522501170.jpg",
                "seafood",
            ),
            Category(
                6,
                "French",
                "https://flexible.img.hani.co.kr/flexible/normal/640/427/imgdb/original/2024/0522/20240522501170.jpg",
                "french",
            ),
        )
}
