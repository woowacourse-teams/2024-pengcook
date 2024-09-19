package net.pengcook.android.presentation.making

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.listener.SpinnerItemChangeListener
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.listener.RecipeMakingEventListener
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class RecipeMakingViewModel(
    private val makingRecipeRepository: MakingRecipeRepository,
) : ViewModel(),
    RecipeMakingEventListener,
    SpinnerItemChangeListener,
    AppbarSingleActionEventListener {
    private val _uiEvent: MutableLiveData<Event<RecipeMakingEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<RecipeMakingEvent>>
        get() = _uiEvent

    private val _categorySelectedValue: MutableLiveData<String> = MutableLiveData()
    val categorySelectedValue: LiveData<String>
        get() = _categorySelectedValue

    val titleContent = MutableLiveData<String>()
    val ingredientContent = MutableLiveData<String>()
    val difficultySelectedValue = MutableLiveData(0.0f)
    val introductionContent = MutableLiveData<String>()
    val hourContent = MutableLiveData<String>()
    val minuteContent = MutableLiveData<String>()
    val secondContent = MutableLiveData<String>()

    private val _currentImage: MutableLiveData<Uri> = MutableLiveData()
    val currentImage: LiveData<Uri>
        get() = _currentImage

    private val _imageUploaded: MutableLiveData<Boolean> = MutableLiveData(false)
    val imageUploaded: LiveData<Boolean>
        get() = _imageUploaded

    private val _imageSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val imageSelected: LiveData<Boolean>
        get() = _imageSelected

    private val _isButtonClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val isButtonClicked: LiveData<Boolean>
        get() = _isButtonClicked

    private var thumbnailTitle: String? = null

    private var recipeId: Long? = null

    init {
        initRecipeDescription()
    }

    fun fetchImageUri(keyName: String) {
        viewModelScope.launch {
            _imageSelected.value = true
            try {
                val uri = makingRecipeRepository.fetchImageUri(keyName)
                _uiEvent.value = Event(RecipeMakingEvent.PresignedUrlRequestSuccessful(uri))
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiEvent.value = Event(RecipeMakingEvent.PostImageFailure)
            }
        }
    }

    fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    ) {
        viewModelScope.launch {
            try {
                makingRecipeRepository.uploadImageToS3(presignedUrl, file)
                thumbnailTitle = file.name
                _uiEvent.value = Event(RecipeMakingEvent.PostImageSuccessful)
                _imageUploaded.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.value = Event(RecipeMakingEvent.PostImageFailure)
            }
        }
    }

    fun changeCurrentImage(uri: Uri) {
        _currentImage.value = uri
    }

    override fun onAddImage() {
        _uiEvent.value = Event(RecipeMakingEvent.AddImage)
    }

    override fun onConfirm() {
        viewModelScope.launch {
            val category = categorySelectedValue.value?.trim()
            val introduction = introductionContent.value?.trim()
            val difficulty = difficultySelectedValue.value
            val ingredients = ingredientContent.value?.trim()
            val title = titleContent.value?.trim()
            val hour = hourContent.value
            val minute = minuteContent.value
            val second = secondContent.value

            if (category.isNullOrEmpty() ||
                introduction.isNullOrEmpty() ||
                difficulty == null ||
                ingredients.isNullOrEmpty() ||
                title.isNullOrEmpty() ||
                thumbnailTitle.isNullOrEmpty() ||
                hour == null ||
                minute == null ||
                second == null
            ) {
                _uiEvent.value = Event(RecipeMakingEvent.DescriptionFormNotCompleted)
                _isButtonClicked.value = true
                return@launch
            }

            saveRecipeDescription(
                category = category,
                introduction = introduction,
                difficulty = difficulty,
                ingredients = ingredients,
                title = title,
                timeRequired =
                    FORMAT_TIME_REQUIRED.format(
                        hour.toIntOrNull() ?: 0,
                        minute.toIntOrNull() ?: 0,
                        second.toIntOrNull() ?: 0,
                    ),
            )
        }
    }

    override fun onSelectionChange(item: String) {
        _categorySelectedValue.value = item
    }

    override fun onNavigateBack() {
        _uiEvent.value = Event(RecipeMakingEvent.MakingCancellation)
    }

    private fun initRecipeDescription() {
        viewModelScope.launch {
            makingRecipeRepository
                .fetchRecipeDescription()
                .onSuccess { existingRecipe ->
                    if (existingRecipe != null) {
                        recipeId = existingRecipe.recipeDescriptionId
                        restoreDescriptionContents(existingRecipe)
                        return@launch
                    }
                    recipeId = System.currentTimeMillis()
                }.onFailure {
                    recipeId = System.currentTimeMillis()
                }
        }
    }

    private fun restoreDescriptionContents(existingRecipe: RecipeDescription) {
        titleContent.value = existingRecipe.title
        ingredientContent.value = existingRecipe.ingredients.joinToString(SEPARATOR_INGREDIENTS)
        difficultySelectedValue.value = existingRecipe.difficulty.toFloat() / 2
        introductionContent.value = existingRecipe.description
        hourContent.value = existingRecipe.cookingTime.split(SEPARATOR_TIME).getOrNull(0) ?: ""
        minuteContent.value = existingRecipe.cookingTime.split(SEPARATOR_TIME).getOrNull(1) ?: ""
        secondContent.value = existingRecipe.cookingTime.split(SEPARATOR_TIME).getOrNull(2) ?: ""
        thumbnailTitle = existingRecipe.thumbnail
        _imageUploaded.value = true
        _imageSelected.value = true
        _categorySelectedValue.value = existingRecipe.categories.first()
        _currentImage.value = Uri.parse(existingRecipe.imageUri)
    }

    private suspend fun saveRecipeDescription(
        category: String,
        introduction: String,
        difficulty: Float,
        ingredients: String,
        title: String,
        timeRequired: String,
    ) {
        val recipeDescription =
            RecipeDescription(
                recipeDescriptionId = recipeId ?: return,
                categories = listOf(category),
                cookingTime = timeRequired,
                description = introduction,
                difficulty = (difficulty * 2).toInt(),
                ingredients = ingredients.split(SEPARATOR_INGREDIENTS).map { it.trim() },
                thumbnail = thumbnailTitle ?: return,
                title = title,
                imageUri = currentImage.value.toString(),
            )

        makingRecipeRepository
            .saveRecipeDescription(recipeDescription)
            .onSuccess { recipeId ->
                _uiEvent.value = Event(RecipeMakingEvent.NavigateToMakingStep(recipeId))
            }.onFailure {
                _uiEvent.value = Event(RecipeMakingEvent.PostRecipeFailure)
            }
    }

    companion object {
        private const val SEPARATOR_INGREDIENTS = ","
        private const val SEPARATOR_TIME = ":"
        private const val FORMAT_TIME_REQUIRED = "%02d:%02d:%02d"
    }
}
