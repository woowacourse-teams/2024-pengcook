package net.pengcook.android.presentation.signup

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!
    private val viewModel: SignUpViewModel by viewModels()
    private val launcher =
        registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()) { uri ->
            try {
                viewModel.changeProfileImage(uri)
            } catch (e: RuntimeException) {

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpBirthDateSpinner()
        setUpCountrySpinner()
        setUpClickListener()
        observeViewModel()
    }

    private fun setUpClickListener() {
        binding.ivProfileImage.setOnClickListener {
            launcher.launch("image/*")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpCountrySpinner() {
        val countryAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                List(100) { "country$it" },
            )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formCountry.spFormContent.spDefault.adapter = countryAdapter
    }

    private fun setUpBirthDateSpinner() {
        val yearAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                List(100) { (it + 1820).toString() },
            )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formBirthDate.spFormContent1.spDefault.adapter = yearAdapter

        val monthAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                List(12) { (it + 1).toString() },
            )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formBirthDate.spFormContent2.spDefault.adapter = monthAdapter

        val dayAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                List(31) { (it + 1).toString() },
            )
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formBirthDate.spFormContent3.spDefault.adapter = dayAdapter
    }

    private fun observeViewModel() {
        viewModel.signUpEvent.observe(viewLifecycleOwner) { event ->
            val signUpEvent = event.getContentIfNotHandled() ?: return@observe
            when (signUpEvent) {
                is SignUpEvent.NavigateToMain -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                }
                is SignUpEvent.Error -> {

                }
                is SignUpEvent.NavigateBack -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_onboardingFragment)
                }
            }
        }
    }
}

