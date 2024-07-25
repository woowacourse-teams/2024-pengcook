package net.pengcook.android.presentation.making.step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.pengcook.android.databinding.FragmentMakingStepBinding

class StepMakingFragment : Fragment() {
    private var _binding: FragmentMakingStepBinding? = null
    private val binding: FragmentMakingStepBinding
        get() = _binding!!
    private val viewModel: StepMakingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMakingStepBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }
}
