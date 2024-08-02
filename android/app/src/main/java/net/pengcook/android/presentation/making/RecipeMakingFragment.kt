package net.pengcook.android.presentation.making

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
import net.pengcook.android.BuildConfig
import net.pengcook.android.data.datasource.makingrecipe.DefaultMakingRecipeRemoteDataSource
import net.pengcook.android.data.remote.api.MakingRecipeService
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.databinding.FragmentRecipeMakingBinding
import net.pengcook.android.presentation.core.util.FileUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class RecipeMakingFragment : Fragment() {
    private var _binding: FragmentRecipeMakingBinding? = null
    private val binding: FragmentRecipeMakingBinding
        get() = _binding!!

    private val viewModel: RecipeMakingViewModel by viewModels {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val makingRecipeService = retrofit.create(MakingRecipeService::class.java)
        val remoteDataSource = DefaultMakingRecipeRemoteDataSource(makingRecipeService)
        val repository = MakingRecipeRepository(remoteDataSource)
        RecipeMakingViewModelFactory(repository)
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
                Toast.makeText(requireContext(), "카메라 권한이 허용되어 있습니다.", Toast.LENGTH_SHORT).show()
                showImageSourceDialog()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private val contentLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                photoUri = it
                currentPhotoPath = FileUtils.getPathFromUri(requireContext(), it)
                if (currentPhotoPath != null) {
                    viewModel.fetchImageUri(File(currentPhotoPath!!).name)
                } else {
                    Toast.makeText(requireContext(), "이미지 선택에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "이미지 선택에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "사진을 찍는데 실패했습니다.", Toast.LENGTH_SHORT).show()
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
        observeData()
    }

    private fun observeData() {
        observeUiEvent()
        observeImageUri()
        observeUploadSuccess()
        observeUploadError()
    }

    private fun observeUploadError() {
        viewModel.uploadError.observe(
            viewLifecycleOwner,
        ) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeUploadSuccess() {
        viewModel.uploadSuccess.observe(
            viewLifecycleOwner,
        ) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "이미지 업로드 성공!", Toast.LENGTH_SHORT).show()
            } else if (success == false) {
                Toast.makeText(requireContext(), "이미지 업로드 실패!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeImageUri() {
        viewModel.imageUri.observe(
            viewLifecycleOwner,
        ) { uri ->
            if (uri != null) {
                uploadImageToS3(uri)
            }
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is MakingEvent.NavigateToMakingStep -> navigateToStepMaking()
                is MakingEvent.AddImage -> addImage()
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
        val options = arrayOf("카메라로 사진 찍기", "앨범에서 사진 선택")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("이미지 소스 선택")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> takePicture()
                    1 -> selectImageFromAlbum()
                }
            }
            .show()
    }

    private fun takePicture() {
        val photoFile: File = createImageFile()
        photoUri =
            FileProvider.getUriForFile(
                requireContext(),
                "net.pengcook.android.fileprovider",
                photoFile,
            )
        takePictureLauncher.launch(photoUri)
    }

    private fun selectImageFromAlbum() {
        contentLauncher.launch("image/*")
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToStepMaking() {
        // val action = RecipeMakingFragmentDirections.actionRecipeMakingFragmentToStepMakingFragment(File(currentPhotoPath!!).name)
        // findNavController().navigate(action)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }
}
