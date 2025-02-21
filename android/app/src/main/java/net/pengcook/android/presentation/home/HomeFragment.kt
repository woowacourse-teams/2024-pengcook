package net.pengcook.android.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.databinding.FragmentHomeBinding
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.core.util.AnalyticsLogging

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val adapter: FeedRecyclerViewAdapter by lazy {
        FeedRecyclerViewAdapter(viewModel, viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("Home")
        initBinding()
        observing()
        initSwipeRefreshLayout()
        binding.mainLogo.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToOldStepMakingFragment(
                    3,
                ),
            )
        }
    }

    private fun initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        viewModel.refreshFeed()

        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun observing() {
        observeFeedData()
        observeUiEvent()
    }

    private fun observeFeedData() {
        viewModel.feedData.observe(viewLifecycleOwner) { pagingData ->
            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is HomeEvent.NavigateToDetail -> {
                    onRecipeClick(newEvent.recipe)
                }

                is HomeEvent.NavigateToProfile -> {
                    val recipe = newEvent.recipe
                    println("navigation to profile")

                    if (recipe.mine) {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                        findNavController().navigate(action)
                    } else {
                        val userId = recipe.user.id
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(
                                userId = userId,
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun onRecipeClick(recipe: RecipeForList) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToDetailRecipeFragment(recipe.recipeId)
        findNavController().navigate(action)
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.homeRcView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadStates ->
                binding.swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
    }
}
