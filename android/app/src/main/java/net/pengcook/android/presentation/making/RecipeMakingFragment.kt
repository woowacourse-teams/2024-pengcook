package net.pengcook.android.presentation.making

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentRecipeMakingBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.util.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class RecipeMakingFragment : Fragment() {
    private var _binding: FragmentRecipeMakingBinding? = null
    private val binding: FragmentRecipeMakingBinding
        get() = _binding!!

    private val viewModel: RecipeMakingViewModel by viewModels {
        val application = (requireContext().applicationContext) as DefaultPengcookApplication
        RecipeMakingViewModelFactory(application.appModule.makingRecipeRepository)
    }

    private lateinit var photoUri: Uri
    private var currentPhotoPath: String? = null

    private val permissionArray =
        arrayOf(
            Manifest.permission.CAMERA,
        )

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                showSnackBar(getString(R.string.camera_permission_granted))
                showImageSourceDialog()
            } else {
                showSnackBar(getString(R.string.camera_permission_needed))
            }
        }

    private val contentLauncher =
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
                    showSnackBar(getString(R.string.image_selection_failed))
                }
            } ?: run {
                showSnackBar(getString(R.string.image_selection_failed))
            }
        }

    // 사진 촬영 ActivityResultLauncher
    private val takePictureLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture(),
        ) { success ->
            if (success) {
                viewModel.fetchImageUri(File(currentPhotoPath!!).name)
            } else {
                showSnackBar(getString(R.string.photo_capture_failed))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecipeMakingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        observeUiEvent()
        setUpCategorySpinner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is MakingEvent.NavigateToMakingStep -> navigateToStepMaking(newEvent.recipeId)
                is MakingEvent.AddImage -> addImage()
                is MakingEvent.PostImageFailure -> showSnackBar(getString(R.string.image_upload_failed))
                is MakingEvent.PostImageSuccessful -> showSnackBar(getString(R.string.image_upload_success))
                is MakingEvent.PresignedUrlRequestSuccessful -> uploadImageToS3(newEvent.presignedUrl)
                is MakingEvent.DescriptionFormNotCompleted -> showSnackBar(getString(R.string.making_warning_form_not_completed))
                is MakingEvent.PostRecipeFailure -> showSnackBar(getString(R.string.making_warning_post_failure))
            }
        }
    }

    private fun addImage() {
        if (permissionArray.all {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it,
                ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            showImageSourceDialog()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf(getString(R.string.take_photo), getString(R.string.choose_from_album))
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(R.string.select_image_source)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePicture()
                    1 -> selectImageFromAlbum()
                }
            }.show()
    }

    private fun takePicture() {
        val photoFile: File = createImageFile()
        photoUri =
            FileProvider.getUriForFile(
                requireContext(),
                "net.pengcook.android.fileprovider",
                photoFile,
            )
        viewModel.changeCurrentImage(photoUri)
        takePictureLauncher.launch(photoUri)
    }

    private fun selectImageFromAlbum() {
        contentLauncher.launch("image/*")
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File
            .createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir,
            ).apply {
                currentPhotoPath = absolutePath
            }
    }

    private fun uploadImageToS3(presignedUrl: String) {
        val file = File(currentPhotoPath!!)
        viewModel.uploadImageToS3(presignedUrl, file)
    }

    private fun navigateToStepMaking(recipeId: Long) {
        val action =
            RecipeMakingFragmentDirections.actionRecipeMakingFragmentToStepMakingFragment(recipeId)
        findNavController().navigate(action)
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
    }

    private fun setUpCategorySpinner() {
        val countryAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.signup_countries),
            )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.makingRecipeCategory.spFormContent.spDefault.adapter = countryAdapter
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
