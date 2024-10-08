package net.pengcook.android.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCategoryBinding
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
        initializeAdapter()
        setUpCategories()
        observeEvents()
    }

    private fun initializeAdapter() {
        binding.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvCategory.layoutManager =
            layoutManager.apply {
                spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (position) {
                                0 -> 3
                                else -> 1
                            }
                        }
                    }
            }
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
    }

    private fun categories() =
        listOf(
            Category(
                id = 100,
                title = getString(R.string.category_culinary_classwars),
                imageUrl = "https://github.com/user-attachments/assets/81327a0a-715d-4c91-b570-43c3386d1866",
                code = "흑백요리사",
            ),
            Category(
                1,
                getString(R.string.category_korean),
                "https://www.90daykorean.com/wp-content/uploads/2015/04/bigstock-Bibimbap-On-A-Concrete-Backgro-449450285-1024x683.jpg",
                "Korean",
            ),
            Category(
                2,
                getString(R.string.category_japanese),
                "https://railrocker.com/playground/wp-content/uploads/2020/02/Onigiri-traditional-Japanese-Rice-Balls.jpg",
                "Japanese",
            ),
            Category(
                3,
                getString(R.string.category_chinese),
                "https://image.yes24.com/images/chyes24/0/a/3/7/0a379fd46d1e404ed63adc3beee52b33.jpg",
                "Chinese",
            ),
        )
}
