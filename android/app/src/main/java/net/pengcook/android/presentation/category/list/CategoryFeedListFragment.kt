package net.pengcook.android.presentation.category.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.databinding.FragmentCategoryFeedListBinding
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFeedListFragment : Fragment() {
    private var _binding: FragmentCategoryFeedListBinding? = null
    private val binding: FragmentCategoryFeedListBinding
        get() = _binding!!
    private val args: CategoryFeedListFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: CategoryFeedListViewModelFactory

    private val viewModel: CategoryFeedListViewModel by viewModels {
        CategoryFeedListViewModel.provideFactory(viewModelFactory, args.category)
    }
    private val adapter: CategoryFeedListAdapter by lazy {
        CategoryFeedListAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoryFeedListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpBindingVariables()
        observeFeedData()
        observeEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpBindingVariables() {
        binding.viewModel = viewModel
        binding.categoryName = args.category
        binding.adapter = adapter
    }

    private fun observeFeedData() {
        viewModel.feedData.observe(viewLifecycleOwner) { pagingData ->
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is CategoryFeedListUiEvent.NavigateBack -> {
                    findNavController().navigateUp()
                }

                is CategoryFeedListUiEvent.NavigateToDetail -> {
                    val action =
                        CategoryFeedListFragmentDirections.actionCategoryFeedListFragmentToDetailRecipeFragment(
                            newEvent.recipe.recipeId,
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }
}
