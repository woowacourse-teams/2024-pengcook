package net.pengcook.android.presentation.making.step

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.IOException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentMakingStepBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.core.util.FileUtils
import net.pengcook.android.presentation.core.util.ImageUtils
import net.pengcook.android.presentation.core.util.MinMaxInputFilter
import net.pengcook.android.presentation.making.newstep.NewStepMakingFragmentArgs
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class StepMakingFragment : Fragment() {
    private var _binding: FragmentMakingStepBinding? = null
    private val binding: FragmentMakingStepBinding
        get() = _binding!!
    private val args: NewStepMakingFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: StepMakingViewModelFactory

    private val viewModel: StepMakingViewModel by viewModels {
        StepMakingViewModel.provideFactory(
            assistedFactory = viewModelFactory,
            recipeId = args.sequence.toLong(),
        )
    }

    private val imageUtils: ImageUtils by lazy { ImageUtils(requireContext()) }

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
                    compressAndFetchPresignedUrl(photoUri)
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
        initTimeFormatInput()
        AnalyticsLogging.viewLogEvent("RecipeMaking")
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.eventHandler = viewModel
        binding.appbarEventListener = viewModel
    }

    private fun initTimeFormatInput() {
        val etMinute = binding.etTimeAmount.etTimeAmountPicker.etMinute
        val etSecond = binding.etTimeAmount.etTimeAmountPicker.etSecond
        arrayOf(MinMaxInputFilter(0, 59)).also { filters ->
            etMinute.filters = filters
            etSecond.filters = filters
        }
    }

    private fun observeViewModel() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is RecipeStepMakingEvent.AddImage -> addImage()
                is RecipeStepMakingEvent.FormNotCompleted -> showToast(getString(R.string.making_warning_form_not_completed))
                is RecipeStepMakingEvent.PostImageFailure -> showToast(getString(R.string.image_upload_failed))
                is RecipeStepMakingEvent.PostImageSuccessful -> showToast(getString(R.string.image_upload_success))
                is RecipeStepMakingEvent.RecipePostFailure -> showToast(getString(R.string.making_warning_post_failure))
                is RecipeStepMakingEvent.PresignedUrlRequestSuccessful -> uploadImageToS3(newEvent.presignedUrl)
                is RecipeStepMakingEvent.ImageNotUploaded -> showToast("Image is being uploaded.")
                is RecipeStepMakingEvent.NavigateBackToDescription -> findNavController().navigateUp()
                is RecipeStepMakingEvent.RecipePostSuccessful -> {
                    /*val action =
                        StepMakingFragmentDirections.actionStepMakingFragmentToHomeFragment()
                    findNavController().navigate(action)*/
                }
            }
        }
    }

    private fun addImage() {
        if (imageUtils.isPermissionGranted(permissionArray)) {
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
        val photoFile: File = imageUtils.createImageFile()
        photoUri = imageUtils.getUriForFile(photoFile)
        currentPhotoPath = photoFile.absolutePath
        viewModel.changeCurrentImage(photoUri)
        takePictureLauncher.launch(photoUri)
    }

    private fun selectImageFromAlbum() {
        contentLauncher.launch("image/*")
    }

    private fun uploadImageToS3(presignedUrl: String) {
        val file = File(currentPhotoPath!!)
        viewModel.uploadImageToS3(presignedUrl, file)
    }

    private fun compressAndFetchPresignedUrl(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val compressedFile = imageUtils.compressAndResizeImage(uri)
                currentPhotoPath = compressedFile.absolutePath
                viewModel.fetchImageUri(File(currentPhotoPath!!).name)
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast(getString(R.string.image_selection_failed))
                }
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
