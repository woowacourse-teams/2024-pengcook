package net.pengcook.android.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentProfileBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val adapter: ProfileAdapter by lazy { ProfileAdapter(viewModel, viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("Profile")
        binding.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        val spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0, 1 -> 3
                        else -> 1
                    }
                }
            }
        layoutManager.spanSizeLookup = spanSizeLookup
        binding.layoutManager = layoutManager
        viewModel.items.observe(viewLifecycleOwner) { pagingData ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                adapter.submitData(pagingData)
            }
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event?.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is ProfileUiEvent.NavigateToEditProfile -> {
                    findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                }

                is ProfileUiEvent.NavigateToRecipeDetail -> {
                    val action =
                        ProfileFragmentDirections.actionProfileFragmentToDetailRecipeFragment(
                            recipe = newEvent.recipe,
                        )
                    findNavController().navigate(action)
                }

                is ProfileUiEvent.NavigateToSetting -> {
                    findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
