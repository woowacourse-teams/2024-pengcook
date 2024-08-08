package net.pengcook.android.presentation.making.step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import net.pengcook.android.databinding.FragmentMakingStepBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.util.AnalyticsLogging

class StepMakingFragment : Fragment() {
    private var _binding: FragmentMakingStepBinding? = null
    private val binding: FragmentMakingStepBinding
        get() = _binding!!
    private val viewModel: StepMakingViewModel by viewModels {
        val appModule =
            (requireContext().applicationContext as DefaultPengcookApplication).appModule
        StepMakingViewModelFactory(
            recipeId = 1,
            maximumStep = 15,
            appModule.recipeStepMakingRepository,
        )
    }

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
        AnalyticsLogging.viewLogEvent("RecipeMaking")
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.eventHandler = viewModel
        binding.appbarEventListener = viewModel
    }

    private fun observeViewModel() {
        observeEmptyIntroductionState()
        observeUploadingImageState()
        observeQuitStepMakingState()
        observeCompleteStepMakingState()
    }

    private fun observeEmptyIntroductionState() {
        viewModel.emptyIntroductionState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                showToast("Introduction Should be filled")
            }
        }
    }

    private fun observeUploadingImageState() {
        viewModel.uploadingImageState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                showToast("Cannot move to next step while uploading image")
            }
        }
    }

    private fun observeQuitStepMakingState() {
        viewModel.quitStepMakingState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigateUp()
            }
        }
    }

    private fun observeCompleteStepMakingState() {
        viewModel.completeStepMakingState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val action = StepMakingFragmentDirections.actionStepMakingFragmentToHomeFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun showToast(message: String) {
        Toast
            .makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT,
            ).show()
    }
}
