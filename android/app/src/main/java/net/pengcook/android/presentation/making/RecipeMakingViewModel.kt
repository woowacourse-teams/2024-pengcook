package net.pengcook.android.presentation.making

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import net.pengcook.android.presentation.core.listener.SpinnerItemChangeListener
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.listener.RecipeMakingEventListener
import java.io.File

class RecipeMakingViewModel(private val makingRecipeRepository: MakingRecipeRepository) :
    ViewModel(), RecipeMakingEventListener, SpinnerItemChangeListener {
    private val _uiEvent: MutableLiveData<Event<MakingEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<MakingEvent>>
        get() = _uiEvent

    private val _categorySelectedValue: MutableLiveData<String> = MutableLiveData()
    val categorySelectedValue: LiveData<String>
        get() = _categorySelectedValue

    val titleContent = MutableLiveData<String>()
    val ingredientContent = MutableLiveData<String>()
    val difficultySelectedValue = MutableLiveData(0.0f)
    val introductionContent = MutableLiveData<String>()

    private val _currentImage: MutableLiveData<Uri> = MutableLiveData()
    val currentImage: LiveData<Uri>
        get() = _currentImage

    private var thumbnailTitle: String? = null

    // Function to fetch a pre-signed URL from the repository
    fun fetchImageUri(keyName: String) {
        viewModelScope.launch {
            try {
                val uri = makingRecipeRepository.fetchImageUri(keyName)
                _uiEvent.value = Event(MakingEvent.PresignedUrlRequestSuccessful(uri))
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.value = Event(MakingEvent.PostImageFailure)
            }
        }
    }

    // Function to upload image to S3
    fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    ) {
        viewModelScope.launch {
            try {
                makingRecipeRepository.uploadImageToS3(presignedUrl, file)
                thumbnailTitle = file.name
                _uiEvent.value = Event(MakingEvent.PostImageSuccessful)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.value = Event(MakingEvent.PostImageFailure)
            }
        }
    }

    fun changeCurrentImage(uri: Uri) {
        _currentImage.value = uri
    }

    override fun onAddImage() {
        _uiEvent.value = Event(MakingEvent.AddImage)
    }

    override fun onConfirm() {
        viewModelScope.launch {
            val category = categorySelectedValue.value?.trim()
            val introduction = introductionContent.value?.trim()
            val difficulty = difficultySelectedValue.value
            val ingredients = ingredientContent.value?.trim()
            val title = titleContent.value?.trim()

            if (category.isNullOrEmpty() ||
                introduction.isNullOrEmpty() ||
                difficulty == null ||
                ingredients.isNullOrEmpty() ||
                title.isNullOrEmpty() ||
                thumbnailTitle.isNullOrEmpty()
            ) {
                _uiEvent.value = Event(MakingEvent.DescriptionFormNotCompleted)
                return@launch
            }

            postRecipeDescription(category, introduction, difficulty, ingredients, title)
        }
    }

    override fun onSelectionChange(item: String) {
        _categorySelectedValue.value = item
    }

    private suspend fun postRecipeDescription(
        category: String,
        introduction: String,
        difficulty: Float,
        ingredients: String,
        title: String,
    ) {
        val recipeDescription =
            RecipeDescription(
                categories = listOf(category),
                cookingTime = "00:00:00",
                description = introduction,
                difficulty = (difficulty * 2).toInt(),
                ingredients = ingredients.split(",").map { it.trim() },
                thumbnail = thumbnailTitle ?: return,
                title = title,
            )

        makingRecipeRepository.postRecipeDescription(recipeDescription)
            .onSuccess { recipeId ->
                _uiEvent.value = Event(MakingEvent.NavigateToMakingStep(recipeId))
            }.onFailure {
                _uiEvent.value = Event(MakingEvent.PostRecipeFailure)
            }
    }
}
