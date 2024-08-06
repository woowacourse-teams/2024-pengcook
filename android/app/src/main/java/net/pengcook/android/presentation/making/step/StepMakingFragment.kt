package net.pengcook.android.presentation.making.step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.pengcook.android.databinding.FragmentMakingStepBinding
import net.pengcook.android.presentation.DefaultPengcookApplication

class StepMakingFragment : Fragment() {
    private var _binding: FragmentMakingStepBinding? = null
    private val binding: FragmentMakingStepBinding
        get() = _binding!!
    private val viewModel: StepMakingViewModel by viewModels {
        /*StepMakingViewModelFactory(
            recipeId = 1,
            maximumStep = 15,
            recipeStepMakingRepository =
                DefaultRecipeStepMakingRepository(
                    recipeStepMakingDataSource =
                        DefaultRecipeStepMakingDataSource(
                            stepMakingService = RetrofitClient.service(StepMakingService::class.java),
                        ),
                ),
        )StepMakingViewModelFactory(
            recipeId = 1,
            maximumStep = 15,
            recipeStepMakingRepository =
                FakeRecipeStepMakingRepository(
                    recipeStepMakingDataSource =
                        FakeRecipeStepMakingDatasource(),
                ),
        )*/

        val appModule = (requireContext().applicationContext as DefaultPengcookApplication).appModule
        StepMakingViewModelFactory(
            recipeId = 1,
            maximumStep = 15,
            appModule.recipeStepMakingRepository,
        )
        // HomeViewModelFactory(appModule.feedRepository)
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
        viewModel.emptyIntroductionState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast
                    .makeText(
                        requireContext(),
                        "Introduction Should be filled",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

        viewModel.uploadingImageState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast
                    .makeText(
                        requireContext(),
                        "Cannot move to next step while uploading image",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

        viewModel.quitStepMakingState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().popBackStack()
            }
        }

        viewModel.completeStepMakingState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                // Seems like legacy code
                findNavController().popBackStack()
                findNavController().popBackStack()
            }
        }
    }
}
