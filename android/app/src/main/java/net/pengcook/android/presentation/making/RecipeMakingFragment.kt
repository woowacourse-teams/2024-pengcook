package net.pengcook.android.presentation.making

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.pengcook.android.databinding.FragmentRecipeMakingBinding

class RecipeMakingFragment : Fragment() {
    private var _binding: FragmentRecipeMakingBinding? = null
    private val binding: FragmentRecipeMakingBinding
        get() = _binding!!
    private val viewModel: RecipeMakingViewModel by viewModels()

    private val permissionArray =
        arrayOf(
            Manifest.permission.CAMERA
        )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "카메라 권한이 허용되어 있습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

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
                is MakingEvent.AddImage -> onAddImageClicked()
            }
        }
    }

    private fun onAddImageClicked() {
        if (permissionArray.all {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            Toast.makeText(requireContext(), "카메라 권한이 허용되어 있습니다.", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onNextClicked() {
        val action = RecipeMakingFragmentDirections.actionRecipeMakingFragmentToStepMakingFragment()
        findNavController().navigate(action)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }
}
