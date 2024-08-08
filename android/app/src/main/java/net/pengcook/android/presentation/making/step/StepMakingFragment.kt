package net.pengcook.android.presentation.making.step

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.navigateUp
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentMakingStepBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.core.util.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class StepMakingFragment : Fragment() {
    private var _binding: FragmentMakingStepBinding? = null
    private val binding: FragmentMakingStepBinding
        get() = _binding!!
    private val args: StepMakingFragmentArgs by navArgs()
    private val viewModel: StepMakingViewModel by viewModels {
        val appModule =
            (requireContext().applicationContext as DefaultPengcookApplication).appModule
        StepMakingViewModelFactory(
            recipeId = args.recipeId,
            maximumStep = MAXIMUM_STEPS,
            recipeStepMakingRepository = appModule.recipeStepMakingRepository,
            makingRecipeRepository = appModule.makingRecipeRepository,
        )
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
                showToast(getString(R.string.camera_permission_granted))
                showImageSourceDialog()
            } else {
                showToast(getString(R.string.camera_permission_needed))
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
                    showToast(getString(R.string.image_selection_failed))
                }
            } ?: run {
                showToast(getString(R.string.image_selection_failed))
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
                showToast(getString(R.string.photo_capture_failed))
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
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
//                is RecipeMakingEvent.NavigateToMakingStep -> navigateToStepMaking(newEvent.recipeId)
//                is RecipeMakingEvent.AddImage -> addImage()
//                is RecipeMakingEvent.PostImageFailure -> showSnackBar(getString(R.string.image_upload_failed))
//                is RecipeMakingEvent.PostImageSuccessful -> showSnackBar(getString(R.string.image_upload_success))
//                is RecipeMakingEvent.PresignedUrlRequestSuccessful -> uploadImageToS3(newEvent.presignedUrl)
//                is RecipeMakingEvent.DescriptionFormNotCompleted -> showSnackBar(getString(R.string.making_warning_form_not_completed))
//                is RecipeMakingEvent.PostRecipeFailure -> showSnackBar(getString(R.string.making_warning_post_failure))
                is RecipeStepMakingEvent.AddImage -> addImage()
                is RecipeStepMakingEvent.DescriptionFormNotCompleted -> showToast(getString(R.string.making_warning_form_not_completed))
                is RecipeStepMakingEvent.PostImageFailure -> showToast(getString(R.string.image_upload_failed))
                is RecipeStepMakingEvent.PostImageSuccessful -> showToast(getString(R.string.image_upload_success))
                is RecipeStepMakingEvent.PostStepFailure -> showToast(getString(R.string.making_warning_post_failure))
                is RecipeStepMakingEvent.PresignedUrlRequestSuccessful -> uploadImageToS3(newEvent.presignedUrl)
            }
        }
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

    companion object {
        private const val MAXIMUM_STEPS = 15
    }
}
