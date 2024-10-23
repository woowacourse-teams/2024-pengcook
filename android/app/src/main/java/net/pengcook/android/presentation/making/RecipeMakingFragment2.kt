package net.pengcook.android.presentation.making

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentRecipeMakingBinding
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.core.util.ImageUtils
import net.pengcook.android.presentation.core.util.MinMaxInputFilter
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class RecipeMakingFragment2 : Fragment() {
    private var _binding: FragmentRecipeMakingBinding? = null
    private val binding: FragmentRecipeMakingBinding
        get() = _binding!!

    private val viewModel: RecipeMakingViewModel2 by viewModels()
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
        _binding = FragmentRecipeMakingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("RecipeMaking")
        initBinding()
        initTimeFormatInput()
        initRecyclerView()
        observeUiEvent()
        observeStepItems()
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
        etHour.filters = arrayOf(MinMaxInputFilter(0, 23))
        arrayOf(MinMaxInputFilter(0, 59)).also { filters ->
            etMinute.filters = filters
            etSecond.filters = filters
        }
    }

    private fun observeStepItems() {
        viewModel.currentStepImages.observe(viewLifecycleOwner) {
            stepImageAdapter.submitList(it)
            stepImageAdapter.currentList.forEach {
                println("sequence : ${it.sequence}")
            }
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is RecipeMakingEvent2.AddThumbnailImage -> addThumbnailImage()
                is RecipeMakingEvent2.AddStepImages -> addStepImages()
                is RecipeMakingEvent2.MakingCancellation -> {
                    findNavController().navigateUp()
                }

                is RecipeMakingEvent2.NullPhotoPath -> showSnackBar(getString(R.string.image_upload_failed))
                is RecipeMakingEvent2.PostImageFailure -> showSnackBar(getString(R.string.image_upload_failed))
                is RecipeMakingEvent2.PostImageSuccessful -> showSnackBar(getString(R.string.image_upload_success))
                is RecipeMakingEvent2.RecipeSavingFailure -> showSnackBar(getString(R.string.making_saving_failure))
                is RecipeMakingEvent2.RecipeSavingSuccessful -> showSnackBar(getString(R.string.making_saving_successful))
                is RecipeMakingEvent2.StepImageSelectionFailure -> showSnackBar(getString(R.string.image_upload_success))
                is RecipeMakingEvent2.UnexpectedError -> showSnackBar(getString(R.string.image_upload_success))
                is RecipeMakingEvent2.ChangeImage -> {
                    targetStepItemId = newEvent.id
                    stepSingleImageLauncher.launch("image/*")
                }

                is RecipeMakingEvent2.ImageDeletionSuccessful -> showSnackBar(getString(R.string.image_deletion_success))
                is RecipeMakingEvent2.DescriptionFormNotCompleted -> showSnackBar(getString(R.string.making_warning_form_not_completed))
                is RecipeMakingEvent2.RecipePostFailure -> showSnackBar(getString(R.string.making_warning_post_failure))
                is RecipeMakingEvent2.RecipePostSuccessful -> findNavController().navigateUp()
                is RecipeMakingEvent2.NavigateToMakingStep -> {
                    val sequence = newEvent.sequence
                    val action =
                        RecipeMakingFragment2Directions.actionRecipeMakingFragmentToStepMakingFragment(1L)
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
