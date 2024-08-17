package net.pengcook.android.presentation.making

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

class RecipeMakingViewModel(private val makingRecipeRepository: MakingRecipeRepository) :
    ViewModel(),
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

    private val _currentImage: MutableLiveData<Uri> = MutableLiveData()
    val currentImage: LiveData<Uri>
        get() = _currentImage

    private val _imageUploaded: MutableLiveData<Boolean> = MutableLiveData(false)
    val imageUploaded: LiveData<Boolean>
        get() = _imageUploaded

    private var thumbnailTitle: String? = null

    private var recipeId: Long? = null

    private val _completed: MediatorLiveData<Boolean> = MediatorLiveData()
    val completed: LiveData<Boolean> = _completed

    init {
        initializeSourceForCompletion()
        initRecipeDescription()
    }

    fun fetchImageUri(keyName: String) {
        viewModelScope.launch {
            try {
                val uri = makingRecipeRepository.fetchImageUri(keyName)
                _uiEvent.value = Event(RecipeMakingEvent.PresignedUrlRequestSuccessful(uri))
            } catch (e: Exception) {
                e.printStackTrace()
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

            if (category.isNullOrEmpty() ||
                introduction.isNullOrEmpty() ||
                difficulty == null ||
                ingredients.isNullOrEmpty() ||
                title.isNullOrEmpty() ||
                thumbnailTitle.isNullOrEmpty()
            ) {
                _uiEvent.value = Event(RecipeMakingEvent.DescriptionFormNotCompleted)
                return@launch
            }

            postRecipeDescription(category, introduction, difficulty, ingredients, title)
        }
    }

    override fun onSelectionChange(item: String) {
        _categorySelectedValue.value = item
    }

    override fun onNavigateBack() {
        _uiEvent.value = Event(RecipeMakingEvent.MakingCancellation)
    }

    private fun initializeSourceForCompletion() {
        _completed.apply {
            addSource(imageUploaded) { _completed.value = completed() }
            addSource(titleContent) { _completed.value = completed() }
            addSource(ingredientContent) { _completed.value = completed() }
            addSource(difficultySelectedValue) { _completed.value = completed() }
            addSource(introductionContent) { _completed.value = completed() }
            addSource(categorySelectedValue) { _completed.value = completed() }
        }
    }

    private fun initRecipeDescription() {
        viewModelScope.launch {
            makingRecipeRepository.fetchRecipeDescription()
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

    private fun completed(): Boolean {
        return imageUploaded.value == true &&
            !categorySelectedValue.value.isNullOrEmpty() &&
            !introductionContent.value.isNullOrEmpty() &&
            difficultySelectedValue.value != null &&
            !ingredientContent.value.isNullOrEmpty() &&
            !titleContent.value.isNullOrEmpty() &&
            !thumbnailTitle.isNullOrEmpty()
    }

    private fun restoreDescriptionContents(existingRecipe: RecipeDescription) {
        titleContent.value = existingRecipe.title
        ingredientContent.value = existingRecipe.ingredients.joinToString(", ")
        difficultySelectedValue.value = existingRecipe.difficulty.toFloat() / 2
        introductionContent.value = existingRecipe.description
        thumbnailTitle = existingRecipe.thumbnail
        _imageUploaded.value = true
        _categorySelectedValue.value = existingRecipe.categories.first()
        _currentImage.value = Uri.parse(existingRecipe.imageUri)
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
                recipeDescriptionId = recipeId ?: return,
                categories = listOf(category),
                cookingTime = "00:00:00",
                description = introduction,
                difficulty = (difficulty * 2).toInt(),
                ingredients = ingredients.split(",").map { it.trim() },
                thumbnail = thumbnailTitle ?: return,
                title = title,
                imageUri = currentImage.value.toString(),
            )

        makingRecipeRepository.saveRecipeDescription(recipeDescription)
            .onSuccess { recipeId ->
                _uiEvent.value = Event(RecipeMakingEvent.NavigateToMakingStep(recipeId))
            }.onFailure {
                _uiEvent.value = Event(RecipeMakingEvent.PostRecipeFailure)
            }
    }
}
