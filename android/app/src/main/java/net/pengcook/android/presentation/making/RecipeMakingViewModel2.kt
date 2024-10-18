package net.pengcook.android.presentation.making

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import net.pengcook.android.presentation.core.listener.AppbarDoubleActionEventListener
import net.pengcook.android.presentation.core.model.RecipeStepMaking
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
        private val stepMakingRepository: RecipeStepMakingRepository,
    ) : ViewModel(),
        RecipeMakingEventListener,
        AppbarDoubleActionEventListener,
        StepItemEventListener {
        private var _isLoading = MutableLiveData<Boolean>(false)
        val isLoading: LiveData<Boolean>
            get() = _isLoading

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
            initRecipeSteps()
        }

        private fun initRecipeSteps() {
            viewModelScope.launch {
                stepMakingRepository.fetchRecipeStepsByRecipeId(recipeId ?: return@launch)
                    .onSuccess { steps ->
                        if (steps != null) {
//                            _currentStepImages.value = steps.map { it.toRecipeStepImage() }
//                            return@launch
                        }
                    }.onFailure {
                    }
            }
        }

        fun changeCurrentThumbnailImage(
            uri: Uri?,
            thumbnailFile: File,
        ) {
            _thumbnailUri.value = uri
            uploadSingleImage(thumbnailFile)
        }

        fun changeCurrentStepImage(
            id: Int,
            uri: Uri?,
            thumbnailFile: File,
        ) {
            _currentStepImages.value =
                currentStepImages.value?.map {
                    if (it.itemId == id) {
                        updateSingleStepImage(it.itemId, thumbnailFile)
                        it.copy(isLoading = true, uri = uri ?: it.uri, file = thumbnailFile)
                    } else {
                        it
                    }
                }
        }

        private fun updateSingleStepImage(
            itemId: Int,
            stepFile: File,
        ) {
            viewModelScope.launch(coroutineExceptionHandler) {
                runCatching {
                    makingRecipeRepository.fetchImageUri(stepFile.name)
                }.onSuccess { presignedUrl ->
                    updateStepImageToServer(presignedUrl, stepFile, itemId)
                }.onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _uiEvent.value = Event(RecipeMakingEvent2.PostImageFailure)
                }
            }
        }

        private suspend fun updateStepImageToServer(
            presignedUrl: String,
            stepFile: File,
            itemId: Int,
        ) {
            runCatching {
                makingRecipeRepository.uploadImageToS3(presignedUrl, stepFile)
            }.onSuccess {
                _uiEvent.value = Event(RecipeMakingEvent2.PostImageSuccessful)
                val changedImage =
                    currentStepImages.value?.find { it.itemId == itemId }?.copy(
                        uploaded = true,
                        isLoading = false,
                    ) ?: return@onSuccess

                _currentStepImages.value =
                    currentStepImages.value?.map {
                        if (it.itemId == itemId) changedImage else it
                    }
            }.onFailure {
                _currentStepImages.value =
                    currentStepImages.value?.map {
                        if (it.itemId == itemId) it.copy(isLoading = false) else it
                    }
                _uiEvent.value = Event(RecipeMakingEvent2.PostImageFailure)
            }
        }

        private fun uploadSingleImage(thumbnailFile: File) {
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
            uploadStepImages()
        }

        private fun uploadStepImages() {
            viewModelScope.launch(coroutineExceptionHandler) {
                val stepImages = currentStepImages.value
                if (stepImages == null) {
                    _uiEvent.value = Event(RecipeMakingEvent2.StepImageSelectionFailure)
                    return@launch
                }

                stepImages.forEach { stepImage ->
                    launch {
                        try {
                            if (!stepImage.uploaded) {
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
                                val changedImage =
                                    try {
                                        uploadImageToS3(presignedUrl, stepImage.file)
                                        stepImage.copy(
                                            uploaded = true,
                                            isLoading = false,
                                        )
                                    } catch (e: Exception) {
                                        if (e is CancellationException) throw e
                                        println("upload failure")
                                        stepImage.copy(
                                            uploaded = false,
                                            isLoading = false,
                                        )
                                    }

                                _currentStepImages.value =
                                    currentStepImages.value?.map {
                                        if (it.itemId == stepImage.itemId) changedImage else it
                                    }
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

        override fun onConfirm() {
            val category = categoryContent.value?.trim()
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
                _uiEvent.value = Event(RecipeMakingEvent2.DescriptionFormNotCompleted)
                _isMakingStepButtonClicked.value = true
                return
            }

            viewModelScope.launch {
                val recipeCreation = recipeCreation()
                if (recipeCreation == null) {
                    _isLoading.value = false
                    _uiEvent.value = Event(RecipeMakingEvent2.RecipePostFailure)
                    return@launch
                }

                makingRecipeRepository.postNewRecipe(recipeCreation)
                    .onSuccess {
                        _isLoading.value = false
                        if (recipeId == null) return@launch
                        makingRecipeRepository.deleteRecipeDescription(recipeId!!)
                        stepMakingRepository.deleteRecipeSteps(recipeId!!)
                        _uiEvent.value = Event(RecipeMakingEvent2.RecipePostSuccessful)
                    }.onFailure {
                        _isLoading.value = false
                        _uiEvent.value = Event(RecipeMakingEvent2.RecipePostFailure)
                    }
            }
        }

        private suspend fun recipeCreation(): RecipeCreation? {
            val recipeData = makingRecipeRepository.fetchTotalRecipeData().getOrNull() ?: return null
            return RecipeCreation(
                title = recipeData.title,
                thumbnail = recipeData.thumbnail,
                cookingTime = recipeData.cookingTime,
                difficulty = recipeData.difficulty,
                ingredients = recipeData.ingredients,
                steps = recipeData.steps,
                categories = recipeData.categories,
                introduction = recipeData.introduction,
            )
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
            difficultySelectedValue.value = existingRecipe.difficulty.toFloat().div(2)
            introductionContent.value = existingRecipe.description
            hourContent.value = existingRecipe.cookingTime.split(SEPARATOR_TIME).getOrNull(0)
            minuteContent.value = existingRecipe.cookingTime.split(SEPARATOR_TIME).getOrNull(1)
            secondContent.value = existingRecipe.cookingTime.split(SEPARATOR_TIME).getOrNull(2)
            thumbnailTitle = existingRecipe.thumbnail
            categoryContent.value = existingRecipe.categories.joinToString()
            _thumbnailUri.value = Uri.parse(existingRecipe.imageUri)
        }

        private fun saveRecipeDescription(
            category: String?,
            introduction: String?,
            difficulty: Float?,
            ingredients: String?,
            title: String?,
            timeRequired: String?,
        ): Job {
            return viewModelScope.launch {
                val recipeDescription =
                    RecipeDescription(
                        recipeDescriptionId = recipeId ?: return@launch,
                        categories =
                            category?.split(SEPARATOR_INGREDIENTS)?.filter {
                                it.trim().isNotEmpty()
                            } ?: emptyList(),
                        cookingTime = timeRequired ?: "",
                        description = introduction ?: "",
                        difficulty = (difficulty?.times(2))?.toInt() ?: 0,
                        ingredients =
                            ingredients?.split(SEPARATOR_INGREDIENTS)
                                ?.filter { it.trim().isNotEmpty() } ?: emptyList(),
                        thumbnail = thumbnailTitle ?: "",
                        title = title ?: "",
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
        }

        override fun onImageChange(id: Int) {
            _uiEvent.value = Event(RecipeMakingEvent2.ChangeImage(id))
        }

        override fun onDelete(id: Int) {
            _currentStepImages.value = currentStepImages.value?.filter { it.itemId != id }
            _uiEvent.value = Event(RecipeMakingEvent2.ImageDeletionSuccessful(id))
        }

        override fun onOrderChange(items: List<RecipeStepImage>) {
            _currentStepImages.value = items
        }

        companion object {
            private const val SEPARATOR_INGREDIENTS = ","
            private const val SEPARATOR_TIME = ":"
            private const val FORMAT_TIME_REQUIRED = "%02d:%02d:%02d"
            private const val EXCEPTION_URL_FILE_SIZE_UNMATCHED =
                "The sizes of files and urls should be the same."
        }

        override fun navigationAction() {
            _uiEvent.value = Event(RecipeMakingEvent2.MakingCancellation)
        }

        override fun customAction() {
            val category = categoryContent.value?.trim()
            val introduction = introductionContent.value?.trim()
            val difficulty = difficultySelectedValue.value
            val ingredients = ingredientContent.value?.trim()
            val title = titleContent.value?.trim()
            val hour = hourContent.value
            val minute = minuteContent.value
            val second = secondContent.value

            val recipeSaving =
                saveRecipeDescription(
                    category = category,
                    introduction = introduction,
                    difficulty = difficulty,
                    ingredients = ingredients,
                    title = title,
                    timeRequired =
                        FORMAT_TIME_REQUIRED.format(
                            hour?.toIntOrNull(),
                            minute?.toIntOrNull(),
                            second?.toIntOrNull(),
                        ),
                )

            val stepSavings =
                currentStepImages.value?.mapIndexed { index, image ->
                    viewModelScope.launch {
                        stepMakingRepository.saveRecipeStep(
                            recipeId!!,
                            RecipeStepMaking(
                                1L,
                                recipeId!!,
                                "",
                                image.file?.name ?: "",
                                index + 1,
                                image.uri.toString(),
                                "00:00:00",
                            ),
                        )
                    }
                }

            viewModelScope.launch {
                recipeSaving.join()
                stepSavings?.joinAll()
            }
        }
    }
