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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentSignUpBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.core.util.FileUtils
import net.pengcook.android.presentation.core.util.ImageUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
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
            module.sessionRepository,
            module.imageRepository,
        )
    }
    private val imageUtils: ImageUtils by lazy { ImageUtils(requireContext()) }
    private lateinit var photoUri: Uri
    private var currentPhotoPath: String? = null

    private val launcher =
        registerForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                photoUri = it
                viewModel.changeCurrentImage(photoUri)
                currentPhotoPath = FileUtils.getPathFromUri(requireContext(), it)
                if (currentPhotoPath != null) {
                    viewModel.fetchImageUri(File(currentPhotoPath!!).name)
                } else {
                    processImageUri(photoUri)
                }
            } ?: run {
                showSnackBar(getString(R.string.image_selection_failed))
            }
        }

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
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("SignUp")
        setUpBindingVariables()
        setUpCountrySpinner()
        observeViewModel()
        observeLoadingStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selectImageFromAlbum() {
        launcher.launch(MIMETYPE_IMAGE)
    }

    private fun uploadImageToS3(presignedUrl: String) {
        val file = File(currentPhotoPath!!)
        viewModel.uploadImageToS3(presignedUrl, file)
    }

    private fun processImageUri(uri: Uri) {
        currentPhotoPath = imageUtils.processImageUri(uri)
        if (currentPhotoPath != null) {
            viewModel.fetchImageUri(File(currentPhotoPath!!).name)
        } else {
            showSnackBar(getString(R.string.image_selection_failed))
        }
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
                is SignUpEvent.UsernameInvalid -> showSnackBar(getString(R.string.signup_message_invalid_username))
                is SignUpEvent.NicknameDuplicated -> showSnackBar(getString(R.string.signup_message_duplicated_username))
                is SignUpEvent.SignUpFormNotCompleted -> showSnackBar(getString(R.string.signup_message_form_not_completed))
                is SignUpEvent.AddImage -> selectImageFromAlbum()
                is SignUpEvent.PostImageFailure -> showSnackBar(getString(R.string.image_upload_failed))
                is SignUpEvent.PostImageSuccessful -> showSnackBar(getString(R.string.image_upload_success))
                is SignUpEvent.PresignedUrlRequestSuccessful -> uploadImageToS3(signUpEvent.presignedUrl)
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
