package net.pengcook.android.presentation.making

import android.Manifest
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
import androidx.lifecycle.Observer
import net.pengcook.android.BuildConfig
import net.pengcook.android.data.datasource.makingrecipe.DefaultMakingRecipeRemoteDataSource
import net.pengcook.android.data.remote.api.MakingRecipeService
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.databinding.FragmentRecipeMakingBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecipeMakingFragment : Fragment() {
    private var _binding: FragmentRecipeMakingBinding? = null
    private val binding: FragmentRecipeMakingBinding
        get() = _binding!!

    private val viewModel: RecipeMakingViewModel by viewModels {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val makingRecipeService = retrofit.create(MakingRecipeService::class.java)
        val remoteDataSource = DefaultMakingRecipeRemoteDataSource(makingRecipeService)
        val repository = MakingRecipeRepository(remoteDataSource)
        RecipeMakingViewModelFactory(repository)
    }

    private lateinit var photoUri: Uri
    private lateinit var currentPhotoPath: String

    private val permissionArray =
        arrayOf(
            Manifest.permission.CAMERA
        )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "카메라 권한이 허용되어 있습니다.", Toast.LENGTH_SHORT).show()
            takePicture()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 사진 촬영 ActivityResultLauncher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.fetchImageUri(File(currentPhotoPath).name)
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
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            when (newEvent) {
                is MakingEvent.NavigateToMakingStep -> onNextClicked()
                is MakingEvent.AddImage -> onAddImageClicked()
            }
        }

        // Observer to handle the pre-signed URL response
        viewModel.imageUri.observe(viewLifecycleOwner, Observer { uri ->
            if (uri != null) {
                uploadImageToS3(uri)
            }
        })

        // Observer to handle the upload success
        viewModel.uploadSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "이미지 업로드 성공!", Toast.LENGTH_SHORT).show()
            } else if (success == false) {
                Toast.makeText(requireContext(), "이미지 업로드 실패!", Toast.LENGTH_SHORT).show()
            }
        })

        // Observer to handle upload error
        viewModel.uploadError.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onAddImageClicked() {
        if (permissionArray.all {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            Toast.makeText(requireContext(), "카메라 권한이 허용되어 있습니다.", Toast.LENGTH_SHORT).show()
            takePicture()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun takePicture() {
        val photoFile: File = createImageFile()
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "net.pengcook.android.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoUri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun uploadImageToS3(presignedUrl: String) {
        val file = File(currentPhotoPath)
        viewModel.uploadImageToS3(presignedUrl, file)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onNextClicked() {
        // val action = RecipeMakingFragmentDirections.actionRecipeMakingFragmentToStepMakingFragment(File(currentPhotoPath).name)
        // findNavController().navigate(action)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }
}
