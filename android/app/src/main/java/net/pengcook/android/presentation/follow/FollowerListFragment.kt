package net.pengcook.android.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.pengcook.android.databinding.FragmentFollowerListBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging

@AndroidEntryPoint
class FollowerListFragment : Fragment() {
    private var _binding: FragmentFollowerListBinding? = null
    private val binding: FragmentFollowerListBinding
        get() = _binding!!
    private val viewModel: FollowerListViewModel by viewModels()
    private val adapter: FollowUserAdapter by lazy {
        FollowUserAdapter(viewModel, viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFollowerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("FollowerList")
        viewModel.pagingDataFlow.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                adapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
