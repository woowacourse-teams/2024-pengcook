package net.pengcook.android.presentation.edit

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentEditRecipeBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.core.util.ImageUtils
import net.pengcook.android.presentation.core.util.MinMaxInputFilter
import net.pengcook.android.presentation.making.ItemMoveCallback
import net.pengcook.android.presentation.making.StepImageAdapter
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class EditRecipeFragment : Fragment() {
    private var _binding: FragmentEditRecipeBinding? = null
    private val binding: FragmentEditRecipeBinding
        get() = _binding!!

    private val args: EditRecipeFragmentArgs by navArgs()

    @Inject
    lateinit var provideFactory: EditRecipeViewModelFactory

    private val viewModel: EditRecipeViewModel by viewModels {
        EditRecipeViewModel.provideFactory(provideFactory, args.recipeId)
    }

    private val stepImageAdapter by lazy { StepImageAdapter(viewModel) }
    private val itemTouchHelper by lazy { ItemTouchHelper(ItemMoveCallback(stepImageAdapter)) }

    private var takenPhotoUri: Uri? = null
    private var takenPhotoPath: String? = null

    private var targetStepItemId: Int? = null

    private val imageUtils: ImageUtils by lazy {
        ImageUtils(requireContext())
    }

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

    private val takePictureLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture(),
        ) { success ->
            val takenPhotoUri = takenPhotoUri
            val takenPhotoPath = takenPhotoPath
            if (success && takenPhotoPath != null && takenPhotoUri != null) {
                viewModel.changeCurrentThumbnailImage(takenPhotoUri, File(takenPhotoPath))
            } else {
                showSnackBar(getString(R.string.photo_capture_failed))
            }
        }

    private val thumbnailImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) {
                showSnackBar(getString(R.string.image_selection_failed))
                return@registerForActivityResult
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val compressedFile = imageUtils.compressAndResizeImage(uri)
                    viewModel.changeCurrentThumbnailImage(uri, compressedFile)
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        showSnackBar(getString(R.string.image_selection_failed))
                    }
                }
            }
        }

    private val stepSingleImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (uri == null) {
                    showSnackBar(getString(R.string.image_selection_failed))
                    return@launch
                }
                try {
                    val compressedFile = imageUtils.compressAndResizeImage(uri)
                    viewModel.changeCurrentStepImage(
                        targetStepItemId ?: return@launch,
                        uri,
                        compressedFile,
                    )
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        showSnackBar(getString(R.string.image_selection_failed))
                    }
                }
            }
        }

    private val requestMultiplePhotoPermissionLauncher =
        registerForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris ->
            viewLifecycleOwner.lifecycleScope.launch {
                val compressedFiles =
                    uris.map { uri ->
                        imageUtils.compressAndResizeImage(uri)
                    }
                viewModel.addStepImages(uris, compressedFiles)
            }
        }

    private val requestMultipleImagesRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                requestMultiplePhotoPermissionLauncher.launch("image/*")
            } else {
                showSnackBar(getString(R.string.camera_permission_needed))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("EditRecipe")
        initBinding()
        initTimeFormatInput()
        initRecyclerView()
        observeUiEvent()
        observeStepItems()
    }

    override fun onStart() {
        super.onStart()
        viewModel.initRecipeSteps()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        binding.rvStepImages.adapter = stepImageAdapter
        itemTouchHelper.attachToRecyclerView(binding.rvStepImages)
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.appbarEventListener = viewModel
    }

    private fun initTimeFormatInput() {
        val etHour = binding.itemTimeRequired.etTimeAmountPicker.etHour
        val etMinute = binding.itemTimeRequired.etTimeAmountPicker.etMinute
        val etSecond = binding.itemTimeRequired.etTimeAmountPicker.etSecond
        etHour.filters = arrayOf(MinMaxInputFilter(0, 23), LengthFilter(2))
        arrayOf(MinMaxInputFilter(0, 59), LengthFilter(2)).also { filters ->
            etMinute.filters = filters
            etSecond.filters = filters
        }
    }

    private fun observeStepItems() {
        viewModel.currentStepImages.observe(viewLifecycleOwner) {
            stepImageAdapter.submitList(it)
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is EditRecipeEvent.AddThumbnailImage -> addThumbnailImage()
                is EditRecipeEvent.AddStepImages -> addStepImages()
                is EditRecipeEvent.EditCancellation -> {
                    findNavController().navigateUp()
                }

                is EditRecipeEvent.NullPhotoPath -> showSnackBar(getString(R.string.image_upload_failed))
                is EditRecipeEvent.PostImageFailure -> showSnackBar(getString(R.string.image_upload_failed))
                is EditRecipeEvent.PostImageSuccessful -> showSnackBar(getString(R.string.image_upload_success))
                is EditRecipeEvent.RecipeSavingFailure -> showSnackBar(getString(R.string.making_saving_failure))
                is EditRecipeEvent.RecipeSavingSuccessful -> showSnackBar(getString(R.string.making_saving_successful))
                is EditRecipeEvent.StepImageSelectionFailure -> showSnackBar(getString(R.string.image_upload_success))
                is EditRecipeEvent.UnexpectedError -> showSnackBar(getString(R.string.image_upload_success))
                is EditRecipeEvent.ChangeImage -> {
                    targetStepItemId = newEvent.id
                    stepSingleImageLauncher.launch("image/*")
                }

                is EditRecipeEvent.ImageDeletionSuccessful -> showSnackBar(getString(R.string.image_deletion_success))
                is EditRecipeEvent.DescriptionFormNotCompleted -> showSnackBar(getString(R.string.making_warning_form_not_completed))
                is EditRecipeEvent.RecipePostFailure -> showSnackBar(getString(R.string.making_warning_post_failure))
                is EditRecipeEvent.RecipePostSuccessful -> findNavController().navigateUp()
                is EditRecipeEvent.NavigateToEditStep -> {
                    val sequence: Int = newEvent.sequence
                    val action =
                        EditRecipeFragmentDirections.actionEditRecipeFragmentToEditStepsFragment(sequence)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun addStepImages() {
        if (imageUtils.isPermissionGranted(permissionArray)) {
            requestMultiplePhotoPermissionLauncher.launch("image/*")
        } else {
            requestMultipleImagesRequestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun addThumbnailImage() {
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
        takenPhotoUri = imageUtils.getUriForFile(photoFile)
        takenPhotoPath = photoFile.absolutePath
        takePictureLauncher.launch(takenPhotoUri)
    }

    private fun selectImageFromAlbum() {
        thumbnailImageLauncher.launch("image/*")
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
