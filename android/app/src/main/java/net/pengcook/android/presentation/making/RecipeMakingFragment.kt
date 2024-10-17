package net.pengcook.android.presentation.making

// import android.Manifest
// import android.app.AlertDialog
// import android.net.Uri
// import android.os.Bundle
// import android.view.LayoutInflater
// import android.view.View
// import android.view.ViewGroup
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.datastore.core.IOException
// import androidx.fragment.app.Fragment
// import androidx.fragment.app.viewModels
// import androidx.lifecycle.lifecycleScope
// import androidx.navigation.fragment.findNavController
// import androidx.recyclerview.widget.ItemTouchHelper
// import com.google.android.material.snackbar.Snackbar
// import dagger.hilt.android.AndroidEntryPoint
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.async
// import kotlinx.coroutines.awaitAll
// import kotlinx.coroutines.launch
// import kotlinx.coroutines.withContext
// import net.pengcook.android.R
// import net.pengcook.android.databinding.FragmentRecipeMakingBinding
// import net.pengcook.android.presentation.core.util.AnalyticsLogging
// import net.pengcook.android.presentation.core.util.FileUtils
// import net.pengcook.android.presentation.core.util.ImageUtils
// import net.pengcook.android.presentation.core.util.MinMaxInputFilter
// import java.io.File
//
// @AndroidEntryPoint
// class RecipeMakingFragment : Fragment() {
//    private var _binding: FragmentRecipeMakingBinding? = null
//    private val binding: FragmentRecipeMakingBinding
//        get() = _binding!!
//
//    private val viewModel: RecipeMakingViewModel by viewModels()
//    private val stepImageAdapter by lazy { StepImageAdapter() }
//    private val itemTouchHelper by lazy { ItemTouchHelper(ItemMoveCallback(stepImageAdapter)) }
//
//    private lateinit var photoUri: Uri
//    private var currentPhotoPath: String? = null
//
//    private var currentStepImagesPath: List<String?> = emptyList()
//
//    private val imageUtils: ImageUtils by lazy {
//        ImageUtils(requireContext())
//    }
//
//    private val permissionArray =
//        arrayOf(
//            Manifest.permission.CAMERA,
//        )
//
//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission(),
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                showSnackBar(getString(R.string.camera_permission_granted))
//                showImageSourceDialog()
//            } else {
//                showSnackBar(getString(R.string.camera_permission_needed))
//            }
//        }
//
//    private val requestMultiplePhotoPermissionLauncher =
//        registerForActivityResult(
//            contract = ActivityResultContracts.GetMultipleContents(),
//        ) { uris ->
//            viewLifecycleOwner.lifecycleScope.launch {
//                viewModel.addCurrentStepImages(uris)
//                currentStepImagesPath =
//                    uris.map {
//                        async {
//                            FileUtils.getPathFromUri(requireContext(), it)
//                        }
//                    }.awaitAll()
//                val fileNames = currentStepImagesPath.map { File(it!!).name }
//
//                viewModel.fetchMultipleImageUris(fileNames)
//            }
//        }
//
//    private val contentLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.GetContent(),
//        ) { uri: Uri? ->
//            uri?.let {
//                photoUri = it
//                viewModel.changeCurrentImage(photoUri)
//                currentPhotoPath = FileUtils.getPathFromUri(requireContext(), it)
//                if (currentPhotoPath != null) {
//                    viewModel.fetchImageUri(File(currentPhotoPath!!).name)
//                } else {
//                    compressAndFetchPresignedUrl(photoUri)
//                }
//            } ?: run {
//                showSnackBar(getString(R.string.image_selection_failed))
//            }
//        }
//
//    // 사진 촬영 ActivityResultLauncher
//    private val takePictureLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.TakePicture(),
//        ) { success ->
//            if (success && currentPhotoPath != null) {
//                viewModel.fetchImageUri(File(currentPhotoPath!!).name)
//            } else {
//                showSnackBar(getString(R.string.photo_capture_failed))
//            }
//        }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        _binding = FragmentRecipeMakingBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?,
//    ) {
//        super.onViewCreated(view, savedInstanceState)
//        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
//        AnalyticsLogging.viewLogEvent("RecipeMaking")
//        initBinding()
//        initTimeFormatInput()
//        observeUiEvent()
//        binding.rvStepImages.adapter = stepImageAdapter
//        itemTouchHelper.attachToRecyclerView(binding.rvStepImages)
// //        setUpCategorySpinner()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun takePicture() {
//        val photoFile: File = imageUtils.createImageFile()
//        photoUri = imageUtils.getUriForFile(photoFile)
//        currentPhotoPath = photoFile.absolutePath
//        viewModel.changeCurrentImage(photoUri)
//        takePictureLauncher.launch(photoUri)
//    }
//
//    private fun compressAndFetchPresignedUrl(uri: Uri) {
//        viewLifecycleOwner.lifecycleScope.launch {
//            try {
//                val compressedFile = imageUtils.compressAndResizeImage(uri)
//                currentPhotoPath = compressedFile.absolutePath
//
//                viewModel.fetchImageUri(File(currentPhotoPath!!).name)
//            } catch (e: IOException) {
//                e.printStackTrace()
//                withContext(Dispatchers.Main) {
//                    showSnackBar(getString(R.string.image_selection_failed))
//                }
//            }
//        }
//    }
//
//    private fun observeUiEvent() {
//        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
//            val newEvent = event.getContentIfNotHandled() ?: return@observe
//            when (newEvent) {
//                is RecipeMakingEvent.NavigateToMakingStep -> navigateToStepMaking(newEvent.recipeId)
//                is RecipeMakingEvent.AddImage -> addImage()
//                is RecipeMakingEvent.PostImageFailure -> showSnackBar(getString(R.string.image_upload_failed))
//                is RecipeMakingEvent.PostImageSuccessful -> showSnackBar(getString(R.string.image_upload_success))
//                is RecipeMakingEvent.PresignedUrlRequestSuccessful -> uploadImageToS3(newEvent.presignedUrl)
//                is RecipeMakingEvent.DescriptionFormNotCompleted -> showSnackBar(getString(R.string.making_warning_form_not_completed))
//                is RecipeMakingEvent.PostRecipeFailure -> showSnackBar(getString(R.string.making_warning_post_failure))
//                is RecipeMakingEvent.MakingCancellation -> findNavController().navigateUp()
//                is RecipeMakingEvent.StepImagesPresignedUrlRequestSuccessful -> {
//                    val urls = newEvent.presignedUrls
//                    val files =
//                        currentStepImagesPath.map { path ->
//                            if (path == null) return@observe
//                            File(path)
//                        }
//                    viewModel.uploadMultipleImagesToS3(presignedUrls = urls, files = files)
//                    stepImageAdapter.submitList(viewModel.currentStepImages)
//                }
//
//                is RecipeMakingEvent.PostStepImageCompleted -> {
//                    println(viewModel.currentStepImages)
//                    stepImageAdapter.submitList(viewModel.currentStepImages)
//                }
//
//                is RecipeMakingEvent.AddStepImages -> addStepImages()
//            }
//        }
//    }
//
//    private fun addImage() {
//        if (imageUtils.isPermissionGranted(permissionArray)) {
//            showImageSourceDialog()
//        } else {
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//    private fun addStepImages() {
//        requestMultiplePhotoPermissionLauncher.launch("image/*")
//    }
//
//    private fun showImageSourceDialog() {
//        val options = arrayOf(getString(R.string.take_photo), getString(R.string.choose_from_album))
//        val builder = AlertDialog.Builder(requireContext())
//        builder
//            .setTitle(R.string.select_image_source)
//            .setItems(options) { _, which ->
//                when (which) {
//                    0 -> takePicture()
//                    1 -> selectImageFromAlbum()
//                }
//            }.show()
//    }
//
//    private fun selectImageFromAlbum() {
//        contentLauncher.launch("image/*")
//    }
//
//    private fun uploadImageToS3(presignedUrl: String) {
//        val file = File(currentPhotoPath!!)
//        viewModel.uploadImageToS3(presignedUrl, file)
//    }
//
//    private fun navigateToStepMaking(recipeId: Long) {
//        val action =
//            RecipeMakingFragmentDirections.actionRecipeMakingFragmentToStepMakingFragment(recipeId)
//        findNavController().navigate(action)
//    }
//
//    private fun initBinding() {
//        binding.lifecycleOwner = viewLifecycleOwner
//        binding.vm = viewModel
//        binding.appbarEventListener = viewModel
//    }
//
//    private fun initTimeFormatInput() {
//        val etHour = binding.itemTimeRequired.etTimeAmountPicker.etHour
//        val etMinute = binding.itemTimeRequired.etTimeAmountPicker.etMinute
//        val etSecond = binding.itemTimeRequired.etTimeAmountPicker.etSecond
//        etHour.filters = arrayOf(MinMaxInputFilter(0, 23))
//        arrayOf(MinMaxInputFilter(0, 59)).also { filters ->
//            etMinute.filters = filters
//            etSecond.filters = filters
//        }
//    }
//
// //    private fun setUpCategorySpinner() {
// //        val countryAdapter =
// //            ArrayAdapter(
// //                requireContext(),
// //                android.R.layout.simple_spinner_item,
// //                resources.getStringArray(R.array.signup_countries),
// //            )
// //        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
// //        binding.makingRecipeCategory.spFormContent.spDefault.adapter = countryAdapter
// //    }
//
//    private fun showSnackBar(message: String) {
//        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
//    }
// }
