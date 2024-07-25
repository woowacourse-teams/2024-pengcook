package net.pengcook.android.presentation.making

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.pengcook.android.databinding.FragmentRecipeMakingBinding

class RecipeMakingFragment : Fragment() {
    private var _binding: FragmentRecipeMakingBinding? = null
    private val binding: FragmentRecipeMakingBinding
        get() = _binding!!
    private val viewModel: RecipeMakingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecipeMakingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is MakingEvent.NavigateToMakingStep -> onNextClicked()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onNextClicked() {
        val action = RecipeMakingFragmentDirections.actionRecipeMakingFragmentToMakingStepFragment()
        findNavController().navigate(action)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }
}
