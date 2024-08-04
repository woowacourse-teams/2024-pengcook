package net.pengcook.android.presentation.signup

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentSignUpBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding!!
    private val args: SignUpFragmentArgs by navArgs()
    private val viewModel: SignUpViewModel by viewModels {
        val application = (requireContext().applicationContext) as DefaultPengcookApplication
        val module = application.appModule
        SignUpViewModelFactory(
            args.platform,
            module.authorizationRepository,
            module.tokenRepository,
        )
    }
    // TODO 사진 받아오는 기능 구현하기
//    private val launcher =
//        registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()) { uri ->
//            changeProfileImage(uri)
//        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpBindingVariables()
        setUpCountrySpinner()
        observeViewModel()
        observeLoadingStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLoadingStatus() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                binding.pbSignUp.visible = isLoading
            }
        }
    }

    private fun setUpBindingVariables() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    // TODO 사진 받아오는 기능 구현
//    private fun setUpClickListener() {
//        binding.ivProfileImage.setOnClickListener {
//            launcher.launch(MIMETYPE_IMAGE)
//        }
//    }

    private fun setUpCountrySpinner() {
        val countryAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.signup_countries),
            )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formCountry.spFormContent.spDefault.adapter = countryAdapter
    }

    private fun changeProfileImage(uri: Uri) {
        try {
            viewModel.changeProfileImage(uri)
        } catch (e: RuntimeException) {
            showSnackBar(getString(R.string.signup_message_error))
        }
    }

    private fun observeViewModel() {
        viewModel.signUpEvent.observe(viewLifecycleOwner) { event ->
            val signUpEvent = event.getContentIfNotHandled() ?: return@observe
            when (signUpEvent) {
                is SignUpEvent.SignInSuccessful -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                }

                is SignUpEvent.Error -> {
                    showSnackBar(getString(R.string.signup_message_error))
                }

                is SignUpEvent.BackPressed -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_onboardingFragment)
                }

                is SignUpEvent.NicknameLengthInvalid -> {
                    showSnackBar(getString(R.string.signup_message_invalid_nickname))
                }

                is SignUpEvent.UsernameInvalid -> {
                    showSnackBar(getString(R.string.signup_message_invalid_username))
                }

                is SignUpEvent.NicknameDuplicated -> {
                    showSnackBar(getString(R.string.signup_message_duplicated_username))
                }

                is SignUpEvent.SignUpFormNotCompleted -> {
                    showSnackBar(getString(R.string.signup_message_form_not_completed))
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    // TODO 사진 멀티파트로 변경하는 작업 data 계층으로 옮기기 + 객체로 분리하기
    private fun convertUriIntoFile(imageUri: Uri): File {
        val bitmap =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.setTargetSampleSize(1)
                    decoder.isMutableRequired = true
                }
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            }

        val resizedBitmap =
            Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)

        val tempFile = File.createTempFile("resized_image", ".jpg", requireContext().cacheDir)
        val fileOutputStream = FileOutputStream(tempFile)
        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.close()
        return tempFile
    }

    companion object {
        private const val MIMETYPE_IMAGE = "image/*"
    }
}
