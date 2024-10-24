package net.pengcook.android.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentSearchBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val adapter: SearchAdapter by lazy { SearchAdapter(viewModel) }
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("Search")
        setUpBindingVariables()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.allRecipes.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event?.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is SearchUiEvent.RecipeSelected -> {
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToDetailRecipeFragment(
                            recipeId = newEvent.recipe.recipeId,
                        )
                    findNavController().navigate(action)
                }

                is SearchUiEvent.SearchFailure -> {
                    showSnackBar(getString(R.string.search_message_failure))
                }
            }
        }
    }

    private fun setUpBindingVariables() {
        binding.lifecycleOwner = this
        binding.adapter = adapter
        binding.viewModel = viewModel
        binding.ablSearchFragment.itemSearchBar.etSearch.setOnEditorActionListener(
            SearchActionListener(),
        )
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
