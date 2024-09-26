package net.pengcook.android.presentation.setting.edit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.IOException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentEditProfileBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.core.util.FileUtils
import net.pengcook.android.presentation.core.util.ImageUtils
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding: FragmentEditProfileBinding
        get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
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
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("Edit")
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
        lifecycleScope.launch {
            try {
                val compressedFile = imageUtils.compressAndResizeImage(uri)
                currentPhotoPath = compressedFile.absolutePath

                withContext(Dispatchers.Main) {
                    viewModel.fetchImageUri(File(currentPhotoPath!!).name)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackBar(getString(R.string.image_selection_failed))
                }
            }
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
            val editProfileEvent = event.getContentIfNotHandled() ?: return@observe
            when (editProfileEvent) {
                is EditProfileEvent.EditProfileSuccessful -> {
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                }

                is EditProfileEvent.Error -> {
                    showSnackBar(getString(R.string.signup_message_error))
                }

                is EditProfileEvent.BackPressed -> {
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                }

                is EditProfileEvent.NicknameLengthInvalid -> {
                    showSnackBar(getString(R.string.signup_message_invalid_nickname))
                }

                is EditProfileEvent.UsernameInvalid -> showSnackBar(getString(R.string.signup_message_invalid_username))
                is EditProfileEvent.NicknameDuplicated -> showSnackBar(getString(R.string.signup_message_duplicated_username))
                is EditProfileEvent.FormNotCompleted -> showSnackBar(getString(R.string.signup_message_form_not_completed))
                is EditProfileEvent.AddImage -> selectImageFromAlbum()
                is EditProfileEvent.PostImageFailure -> showSnackBar(getString(R.string.image_upload_failed))
                is EditProfileEvent.PostImageSuccessful -> showSnackBar(getString(R.string.image_upload_success))
                is EditProfileEvent.PresignedUrlRequestSuccessful ->
                    uploadImageToS3(
                        editProfileEvent.presignedUrl,
                    )
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val MIMETYPE_IMAGE = "image/*"
    }
}
