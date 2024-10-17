package net.pengcook.android.presentation.making

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.listener.RecipeMakingEventListener
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class RecipeMakingViewModel2
    @Inject
    constructor(
        private val makingRecipeRepository: MakingRecipeRepository,
    ) : ViewModel(),
        RecipeMakingEventListener,
        AppbarSingleActionEventListener {
        private val _uiEvent: MutableLiveData<Event<RecipeMakingEvent2>> = MutableLiveData()
        val uiEvent: LiveData<Event<RecipeMakingEvent2>>
            get() = _uiEvent

        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                _uiEvent.value = Event(RecipeMakingEvent2.UnexpectedError)
            }

        // thumbnail
        private val _thumbnailUri: MutableLiveData<Uri?> = MutableLiveData(null)
        val thumbnailUri: LiveData<Uri?>
            get() = _thumbnailUri
        private var thumbnailTitle: String? = null

        // step
        private val _currentStepImages: MutableLiveData<List<RecipeStepImage>> =
            MutableLiveData(
                emptyList(),
            )
        val currentStepImages: LiveData<List<RecipeStepImage>>
            get() = _currentStepImages

        val categoryContent = MutableLiveData<String>()
        val titleContent = MutableLiveData<String>()
        val ingredientContent = MutableLiveData<String>()
        val difficultySelectedValue = MutableLiveData(0.0f)
        val introductionContent = MutableLiveData<String>()
        val hourContent = MutableLiveData<String>()
        val minuteContent = MutableLiveData<String>()
        val secondContent = MutableLiveData<String>()

        private val _isMakingStepButtonClicked: MutableLiveData<Boolean> = MutableLiveData(false)
        val isMakingStepButtonClicked: LiveData<Boolean>
            get() = _isMakingStepButtonClicked

        private var recipeId: Long? = null

        init {
            initRecipeDescription()
        }

        fun changeCurrentThumbnailImage(
            uri: Uri?,
            thumbnailFile: File,
        ) {
            _thumbnailUri.value = uri
            fetchThumbnailImageUri(thumbnailFile)
        }

        private fun fetchThumbnailImageUri(thumbnailFile: File) {
            viewModelScope.launch(coroutineExceptionHandler) {
                runCatching {
                    makingRecipeRepository.fetchImageUri(thumbnailFile.name)
                }.onSuccess { presignedUrl ->
                    uploadImageToS3(presignedUrl = presignedUrl, file = thumbnailFile)
                }.onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _uiEvent.value = Event(RecipeMakingEvent2.PostImageFailure)
                }
            }
        }

        private suspend fun uploadImageToS3(
            presignedUrl: String,
            file: File,
        ) {
            runCatching {
                makingRecipeRepository.uploadImageToS3(presignedUrl, file)
            }.onSuccess {
                thumbnailTitle = file.name
                _uiEvent.value = Event(RecipeMakingEvent2.PostImageSuccessful)
//            _imageUploaded.value = true
            }.onFailure {
                _uiEvent.value = Event(RecipeMakingEvent2.PostImageFailure)
            }
        }

        fun addStepImages(
            uris: List<Uri>,
            thumbnailFiles: List<File>,
        ) {
            require(uris.size == thumbnailFiles.size) {
                EXCEPTION_URL_FILE_SIZE_UNMATCHED
            }

            val images =
                uris.zip(thumbnailFiles) { uri, thumbnailFile ->
                    RecipeStepImage.of(uri, thumbnailFile)
                }
            _currentStepImages.value = currentStepImages.value?.plus(images)
        }

        private fun uploadStepImages() {
            viewModelScope.launch(coroutineExceptionHandler) {
                val stepImages = currentStepImages.value
                if (stepImages == null) {
                    _uiEvent.value = Event(RecipeMakingEvent2.StepImageSelectionFailure)
                    return@launch
                }

                stepImages.map { stepImage ->
                    launch {
                        try {
                            val fileName =
                                stepImage.file?.name ?: run {
                                    val changedImage =
                                        stepImage.copy(
                                            uploaded = false,
                                            isLoading = false,
                                        )
                                    _currentStepImages.value =
                                        currentStepImages.value?.map {
                                            if (it.itemId == stepImage.itemId) changedImage else it
                                        }

                                    return@launch
                                }
                            val presignedUrl = makingRecipeRepository.fetchImageUri(fileName)
                            uploadImageToS3(presignedUrl, stepImage.file)
                            val changedImage =
                                stepImage.copy(
                                    uploaded = true,
                                    isLoading = false,
                                )
                            _currentStepImages.value =
                                currentStepImages.value?.map {
                                    if (it.itemId == stepImage.itemId) changedImage else it
                                }
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            _uiEvent.value = Event(RecipeMakingEvent2.StepImageSelectionFailure)
                            val changedImage =
                                stepImage.copy(
                                    uploaded = false,
                                    isLoading = false,
                                )

                            _currentStepImages.value =
                                currentStepImages.value?.map {
                                    if (it.itemId == stepImage.itemId) changedImage else it
                                }
                        }
                    }
                }
            }
        }

        override fun onAddImage() {
            _uiEvent.value = Event(RecipeMakingEvent2.AddThumbnailImage)
        }

        override fun onAddStepImages() {
            _uiEvent.value = Event(RecipeMakingEvent2.AddStepImages)
        }

        // 우측 상단 저장 버튼으로 변경 필요
        override fun onConfirm() {
        }

        override fun onNavigateBack() {
            _uiEvent.value = Event(RecipeMakingEvent2.MakingCancellation)
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
//        _imageUploaded.value = true
//        _imageSelected.value = true
            categoryContent.value = existingRecipe.categories.joinToString()
            _thumbnailUri.value = Uri.parse(existingRecipe.imageUri)
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
                    categories = category.split(SEPARATOR_INGREDIENTS).map { it.trim() },
                    cookingTime = timeRequired,
                    description = introduction,
                    difficulty = (difficulty * 2).toInt(),
                    ingredients = ingredients.split(SEPARATOR_INGREDIENTS).map { it.trim() },
                    thumbnail = thumbnailTitle ?: return,
                    title = title,
                    imageUri = thumbnailUri.value.toString(),
                )

            makingRecipeRepository
                .saveRecipeDescription(recipeDescription)
                .onSuccess { _ ->
                    _uiEvent.value = Event(RecipeMakingEvent2.RecipeSavingSuccessful)
                }.onFailure {
                    _uiEvent.value = Event(RecipeMakingEvent2.RecipeSavingFailure)
                }
        }

        companion object {
            private const val SEPARATOR_INGREDIENTS = ","
            private const val SEPARATOR_TIME = ":"
            private const val FORMAT_TIME_REQUIRED = "%02d:%02d:%02d"
            private const val EXCEPTION_URL_FILE_SIZE_UNMATCHED =
                "The sizes of files and urls should be the same."
        }
    }

data class RecipeStepImage(
    val itemId: Int,
    val isLoading: Boolean = true,
    val file: File?,
    val uri: Uri,
    val uploaded: Boolean = false,
) {
    companion object {
        private var id: Int = 0

        fun of(
            uri: Uri,
            file: File? = null,
        ): RecipeStepImage {
            return RecipeStepImage(
                itemId = id++,
                file = file,
                uri = uri,
            )
        }
    }
}
