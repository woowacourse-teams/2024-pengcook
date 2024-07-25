package net.pengcook.android.presentation.category.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.pengcook.android.databinding.FragmentCategoryFeedListBinding

class CategoryFeedListFragment : Fragment() {
    private var _binding: FragmentCategoryFeedListBinding? = null
    private val binding: FragmentCategoryFeedListBinding
        get() = _binding!!
    private val viewModel: CategoryFeedListViewModel by viewModels()
    private val adapter: CategoryFeedListAdapter by lazy {
        CategoryFeedListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoryFeedListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.categoryName = "smapleCategory"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
