package net.pengcook.android.presentation.making

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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

        private fun initRecipeDescription() {
            viewModelScope.launch {
                makingRecipeRepository.fetchRecipeDescription()
                    .onSuccess { existingRecipe ->
                        existingRecipe?.let {
                            recipeId = it.recipeDescriptionId
                            restoreDescriptionContents(it)
                        } ?: run {
                            recipeId = System.currentTimeMillis()
                        }
                    }.onFailure {
                        recipeId = System.currentTimeMillis()
                    }
            }
        }

        private fun initRecipeSteps() {
            viewModelScope.launch {
                stepMakingRepository.fetchRecipeStepsByRecipeId()
                    .onSuccess { steps ->
                        _currentStepImages.value = steps?.map { it.toRecipeStepImage() } ?: emptyList()
                    }.onFailure {
                        _currentStepImages.value = emptyList()
                    }
            }
        }

        private fun RecipeStepMaking.toRecipeStepImage() =
            RecipeStepImage(
                itemId = stepId.toInt(),
                uri = Uri.parse(imageUri),
                imageTitle = image,
                uploaded = imageUploaded,
                isLoading = false,
                file = File(imageUri),
                sequence = sequence,
            )

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

        private fun uploadSingleImage(file: File) {
            viewModelScope.launch(coroutineExceptionHandler) {
                val presignedUrl = makingRecipeRepository.fetchImageUri(file.name)
                uploadImageToS3(presignedUrl, file)
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
            require(uris.size == thumbnailFiles.size) { EXCEPTION_URL_FILE_SIZE_UNMATCHED }

            val currSize = currentStepImages.value?.size ?: 0
            val newImages =
                uris.zip(thumbnailFiles).mapIndexed { index, (uri, file) ->
                    RecipeStepImage.of(
                        uri = uri,
                        file = file,
                        sequence = currSize + index + 1,
                        title = file.name,
                    )
                }
            _currentStepImages.value = currentStepImages.value?.plus(newImages)
            uploadStepImages()
        }

        private fun uploadStepImages() {
            viewModelScope.launch(coroutineExceptionHandler) {
                currentStepImages.value?.forEach { stepImage ->
                    if (!stepImage.uploaded) {
                        uploadStepImage(stepImage)
                    }
                }
            }
        }

        private suspend fun uploadStepImage(stepImage: RecipeStepImage) {
            val fileName =
                stepImage.file?.name ?: run {
                    updateStepImageState(stepImage.copy(uploaded = false, isLoading = false))
                    return
                }

            try {
                val presignedUrl = makingRecipeRepository.fetchImageUri(fileName)
                uploadImageToS3(presignedUrl, stepImage.file)
                updateStepImageState(stepImage.copy(uploaded = true, isLoading = false))
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                updateStepImageState(stepImage.copy(uploaded = false, isLoading = false))
                _uiEvent.value = Event(RecipeMakingEvent2.StepImageSelectionFailure)
            }
        }

        private fun updateStepImageState(updatedImage: RecipeStepImage) {
            _currentStepImages.value =
                currentStepImages.value?.map {
                    if (it.itemId == updatedImage.itemId) updatedImage else it
                }
        }

        override fun onConfirm() {
            if (!validateDescriptionForm()) {
                _uiEvent.value = Event(RecipeMakingEvent2.DescriptionFormNotCompleted)
                _isMakingStepButtonClicked.value = true
                return
            }

            if (currentStepImages.value.isNullOrEmpty()) {
                _uiEvent.value = Event(RecipeMakingEvent2.DescriptionFormNotCompleted)
                return
            }

            _isLoading.value = true

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
                        recipeId?.let {
                            makingRecipeRepository.deleteRecipeDescription(it)
                            stepMakingRepository.deleteRecipeSteps(it)
                        }
                        _uiEvent.value = Event(RecipeMakingEvent2.RecipePostSuccessful)
                    }.onFailure {
                        _isLoading.value = false
                        _uiEvent.value = Event(RecipeMakingEvent2.RecipePostFailure)
                    }
            }
        }

        private fun validateDescriptionForm(): Boolean {
            return !categoryContent.value.isNullOrBlank() &&
                !introductionContent.value.isNullOrBlank() &&
                difficultySelectedValue.value != null &&
                !ingredientContent.value.isNullOrBlank() &&
                !titleContent.value.isNullOrBlank() &&
                !thumbnailTitle.isNullOrBlank() &&
                hourContent.value != null &&
                minuteContent.value != null &&
                secondContent.value != null
        }

        private suspend fun recipeCreation(): RecipeCreation? {
            return makingRecipeRepository.fetchTotalRecipeData().getOrNull()?.let { recipeData ->
                RecipeCreation(
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
        }

        private fun restoreDescriptionContents(existingRecipe: RecipeDescription) {
            with(existingRecipe) {
                titleContent.value = title
                ingredientContent.value = ingredients.joinToString(SEPARATOR_INGREDIENTS)
                difficultySelectedValue.value = difficulty.toFloat() / 2
                introductionContent.value = description
                val timeParts = cookingTime.split(SEPARATOR_TIME)
                hourContent.value = timeParts.getOrNull(0)
                minuteContent.value = timeParts.getOrNull(1)
                secondContent.value = timeParts.getOrNull(2)
                thumbnailTitle = thumbnail
                categoryContent.value = categories.joinToString()
                _thumbnailUri.value = Uri.parse(imageUri)
            }
        }

        override fun customAction() {
            viewModelScope.launch {
                recipeId?.let { id ->
                    stepMakingRepository.deleteRecipeStepsNonAsync(id)
                    saveRecipeDescription()
                    saveRecipeSteps(id)
                }
            }
        }

        private suspend fun saveRecipeDescription() {
            val recipeDescription =
                RecipeDescription(
                    recipeDescriptionId = recipeId ?: return,
                    categories =
                        categoryContent.value?.split(SEPARATOR_INGREDIENTS)
                            ?.filter { it.trim().isNotEmpty() || it.trim().isNotBlank() } ?: emptyList(),
                    cookingTime = formatTimeRequired(),
                    description = introductionContent.value ?: "",
                    difficulty = (difficultySelectedValue.value?.times(2))?.toInt() ?: 0,
                    ingredients =
                        ingredientContent.value?.split(SEPARATOR_INGREDIENTS)
                            ?.filter { it.trim().isNotEmpty() || it.trim().isNotBlank() } ?: emptyList(),
                    thumbnail = thumbnailTitle ?: "",
                    title = titleContent.value ?: "",
                    imageUri = thumbnailUri.value.toString(),
                )

            makingRecipeRepository.saveRecipeDescription(recipeDescription)
                .onSuccess { _uiEvent.value = Event(RecipeMakingEvent2.RecipeSavingSuccessful) }
                .onFailure { _uiEvent.value = Event(RecipeMakingEvent2.RecipeSavingFailure) }
        }

        private suspend fun saveRecipeSteps(recipeId: Long) {
            currentStepImages.value?.forEachIndexed { index, image ->
                stepMakingRepository.saveRecipeStep(
                    recipeId,
                    RecipeStepMaking(
                        1L,
                        recipeId,
                        image.description,
                        image.imageTitle,
                        index + 1,
                        image.uri.toString(),
                        image.cookingTime,
                        imageUploaded = image.uploaded,
                    ),
                )
            }
        }

        private fun formatTimeRequired(): String {
            return FORMAT_TIME_REQUIRED.format(
                hourContent.value?.toIntOrNull() ?: 0,
                minuteContent.value?.toIntOrNull() ?: 0,
                secondContent.value?.toIntOrNull() ?: 0,
            )
        }

        override fun onAddImage() {
            _uiEvent.value = Event(RecipeMakingEvent2.AddThumbnailImage)
        }

        override fun onAddStepImages() {
            _uiEvent.value = Event(RecipeMakingEvent2.AddStepImages)
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

        override fun navigationAction() {
            _uiEvent.value = Event(RecipeMakingEvent2.MakingCancellation)
        }

        companion object {
            private const val SEPARATOR_INGREDIENTS = ","
            private const val SEPARATOR_TIME = ":"
            private const val FORMAT_TIME_REQUIRED = "%02d:%02d:%02d"
            private const val EXCEPTION_URL_FILE_SIZE_UNMATCHED =
                "The sizes of files and urls should be the same."
        }
    }