package net.pengcook.android.presentation.making

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.listener.RecipeMakingEventListener
import java.io.File

class RecipeMakingViewModel(private val makingRecipeRepository: MakingRecipeRepository) :
    ViewModel(), RecipeMakingEventListener {
    private val _uiEvent: MutableLiveData<Event<MakingEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<MakingEvent>>
        get() = _uiEvent

    val titleContent = MutableLiveData<String>()
    val categorySelectedValue = MutableLiveData<String>()
    val ingredientContent = MutableLiveData<String>()
    val difficultySelectedValue = MutableLiveData<String>()
    val introductionContent = MutableLiveData<String>()

    private val _imageUri = MutableLiveData<String>()
    val imageUri: LiveData<String>
        get() = _imageUri
    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean>
        get() = _uploadSuccess

    private val _uploadError = MutableLiveData<String>()
    val uploadError: LiveData<String>
        get() = _uploadError

    override fun onNavigateToMakingStep() {
        _uiEvent.value = Event(MakingEvent.NavigateToMakingStep)
    }

    override fun onAddImage() {
        _uiEvent.value = Event(MakingEvent.AddImage)
    }

    // Function to fetch a pre-signed URL from the repository
    fun fetchImageUri(keyName: String) {
        viewModelScope.launch {
            try {
                val uri = makingRecipeRepository.fetchImageUri(keyName)
                _imageUri.postValue(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadError.postValue("Pre-signed URL 요청 실패: ${e.message}")
            }
        }
    }

    // Function to upload image to S3
    fun uploadImageToS3(presignedUrl: String, file: File) {
        viewModelScope.launch {
            try {
                makingRecipeRepository.uploadImageToS3(presignedUrl, file)
                _uploadSuccess.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadSuccess.postValue(false)
                _uploadError.postValue("이미지 업로드 실패: ${e.message}")
            }
        }
    }
}

sealed interface MakingEvent {
    data object NavigateToMakingStep : MakingEvent
    data object AddImage : MakingEvent
}