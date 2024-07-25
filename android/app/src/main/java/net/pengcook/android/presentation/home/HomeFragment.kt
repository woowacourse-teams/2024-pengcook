package net.pengcook.android.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.data.datasource.feed.DefaultFeedRemoteDataSource
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.repository.feed.DefaultFeedRepository
import net.pengcook.android.data.util.network.RetrofitClient
import net.pengcook.android.databinding.FragmentHomeBinding

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
        observeFeedData()
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

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.homeRcView.adapter = adapter
    }
}
