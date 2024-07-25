package net.pengcook.android.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.data.datasource.feed.DefaultFeedRemoteDataSource
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.repository.feed.DefaultFeedRepository
import net.pengcook.android.data.util.network.RetrofitClient
import net.pengcook.android.databinding.FragmentHomeBinding
import net.pengcook.android.presentation.core.model.Recipe

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            DefaultFeedRepository(
                DefaultFeedRemoteDataSource(
                    RetrofitClient.service(
                        FeedService::class.java,
                    ),
                ),
            ),
        )
    }
    private lateinit var binding: FragmentHomeBinding
    private val adapter: FeedRecyclerViewAdapter by lazy {
        FeedRecyclerViewAdapter(viewModel)
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

        initBinding()
        observing()
    }

    private fun observing() {
        observeFeedData()
        observeUiEvent()
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is HomeEvent.NavigateToDetail -> {
                    onSingleMovieClicked(newEvent.recipe)
                }
            }
        }
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

    private fun onSingleMovieClicked(recipe: Recipe) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailRecipeFragment(recipe)
        findNavController().navigate(action)
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.homeRcView.adapter = adapter
    }
}
