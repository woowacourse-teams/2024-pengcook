package net.pengcook.android.presentation.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.edit.EditRecipeRepository
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.domain.model.recipemaking.RecipeDescription
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.model.ChangedRecipe
import net.pengcook.android.presentation.core.model.Ingredient
import net.pengcook.android.presentation.core.model.RecipeStep
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.RecipeStepImage
import net.pengcook.android.presentation.making.StepItemEventListener
import net.pengcook.android.presentation.making.listener.RecipeMakingEventListener
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class EditRecipeViewModel
    @AssistedInject
    constructor(
        @Assisted private val recipeId: Long,
        private val feedRepository: FeedRepository,
        private val makingRecipeRepository: MakingRecipeRepository,
    ) : ViewModel(),
        RecipeMakingEventListener,
        AppbarSingleActionEventListener,
        StepItemEventListener {
        private var _isLoading = MutableLiveData<Boolean>(false)
        val isLoading: LiveData<Boolean>
            get() = _isLoading

        private val _uiEvent: MutableLiveData<Event<EditRecipeEvent>> = MutableLiveData()
        val uiEvent: LiveData<Event<EditRecipeEvent>>
            get() = _uiEvent

        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                _uiEvent.value = Event(EditRecipeEvent.UnexpectedError)
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

        init {
            initRecipeDescription()
            initRecipeSteps()
        }

        private fun initRecipeDescription() {
            viewModelScope.launch {
                feedRepository.fetchRecipe(recipeId).onSuccess { recipe ->
                    categoryContent.value = recipe.category.joinToString()
                    titleContent.value = recipe.title
                    ingredientContent.value = recipe.ingredients.joinToString { it.ingredientName }
                    difficultySelectedValue.value = recipe.difficulty / 2f
                    introductionContent.value = recipe.introduction
                    hourContent.value = recipe.cookingTime.split(":")[0]
                    minuteContent.value = recipe.cookingTime.split(":")[1]
                    secondContent.value = recipe.cookingTime.split(":")[2]
                    _thumbnailUri.value = Uri.parse(recipe.thumbnail)
                    thumbnailTitle = recipe.thumbnail.split("/").lastOrNull() ?: run {
                        _uiEvent.value = Event(EditRecipeEvent.RecipePostFailure)
                        return@launch
                    }
                }
            }
        }

        fun initRecipeSteps() {
            viewModelScope.launch {
                feedRepository.fetchRecipeSteps(recipeId).onSuccess { steps ->
                    println("steps$steps")
                    _currentStepImages.value =
                        steps.map {
                            RecipeStepImage(
                                itemId = it.stepId.toInt(),
                                uri = Uri.parse(it.image),
                                description = it.description,
                                cookingTime = it.cookingTime,
                                isLoading = false,
                                sequence = it.sequence,
                                file = null,
                                uploaded = true,
                                imageTitle =
                                    it.image.split("/").lastOrNull() ?: run {
                                        _uiEvent.value = Event(EditRecipeEvent.RecipePostFailure)
                                        return@launch
                                    },
                            )
                        }
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
                    _uiEvent.value = Event(EditRecipeEvent.PostImageFailure)
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
                _uiEvent.value = Event(EditRecipeEvent.PostImageSuccessful)
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
                _uiEvent.value = Event(EditRecipeEvent.PostImageFailure)
            }
        }

        fun changeCurrentThumbnailImage(
            uri: Uri?,
            thumbnailFile: File,
        ) {
            _thumbnailUri.value = uri
            uploadSingleThumbnailImage(thumbnailFile)
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

        private fun uploadSingleThumbnailImage(file: File) {
            viewModelScope.launch(coroutineExceptionHandler) {
                val presignedUrl = makingRecipeRepository.fetchImageUri(file.name)
                uploadThumbnailImageToS3(presignedUrl, file)
            }
        }

        private suspend fun uploadThumbnailImageToS3(
            presignedUrl: String,
            file: File,
        ) {
            runCatching {
                makingRecipeRepository.uploadImageToS3(presignedUrl, file)
            }.onSuccess {
                thumbnailTitle = file.name
            }.onFailure {
                _uiEvent.value = Event(EditRecipeEvent.PostImageFailure)
            }
        }

        private suspend fun uploadImageToS3(
            presignedUrl: String,
            file: File,
        ) {
            runCatching {
                makingRecipeRepository.uploadImageToS3(presignedUrl, file)
            }.onFailure {
                _uiEvent.value = Event(EditRecipeEvent.PostImageFailure)
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
            currentStepImages.value?.forEach { stepImage ->
                viewModelScope.launch(coroutineExceptionHandler) {
                    if (!stepImage.uploaded) {
                        uploadStepImage(stepImage)
                        saveRecipeDescription()
                        saveRecipeSteps(recipeId)
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
                _uiEvent.value = Event(EditRecipeEvent.StepImageSelectionFailure)
            }
        }

        private fun updateStepImageState(updatedImage: RecipeStepImage) {
            _currentStepImages.value =
                currentStepImages.value?.map {
                    if (it.itemId == updatedImage.itemId) updatedImage else it
                }
        }

        override fun onConfirm() {
            val steps = EditRecipeRepository.fetchAllSavedRecipeData().getOrNull()?.steps

            val recipeCreation =
                RecipeCreation(
                    title = titleContent.value ?: "",
                    thumbnail = thumbnailTitle ?: "",
                    cookingTime = formatTimeRequired(),
                    difficulty = (difficultySelectedValue.value?.times(2))?.toInt() ?: 0,
                    ingredients =
                        ingredientContent.value
                            ?.split(SEPARATOR_INGREDIENTS)
                            ?.filter { it.trim().isNotEmpty() || it.trim().isNotBlank() }
                            ?.map { it.trim() }
                            ?.map {
                                Ingredient(
                                    ingredientId = 1L,
                                    ingredientName = it,
                                    requirement = "REQUIRED",
                                )
                            } ?: emptyList(),
                    steps =
                        steps?.map {
                            RecipeStepMaking(
                                stepId = it.stepId,
                                recipeId = recipeId,
                                description = it.description,
                                image = it.image,
                                sequence = it.sequence,
                                imageUri = it.imageUri,
                                cookingTime = it.cookingTime,
                                imageUploaded = it.imageUploaded,
                            )
                        } ?: emptyList(),
                    categories =
                        categoryContent.value
                            ?.split(SEPARATOR_INGREDIENTS)
                            ?.filter { it.trim().isNotEmpty() || it.trim().isNotBlank() }
                            ?.map { it.trim() } ?: emptyList(),
                    introduction = introductionContent.value ?: "",
                )

            EditRecipeRepository
                .saveRecipe(recipeCreation)
                .onFailure { _uiEvent.value = Event(EditRecipeEvent.RecipeSavingFailure) }

            if (!validateDescriptionForm()) {
                _uiEvent.value = Event(EditRecipeEvent.DescriptionFormNotCompleted)
                _isMakingStepButtonClicked.value = true
                return
            }

            val entireData = EditRecipeRepository.fetchAllSavedRecipeData().getOrNull() ?: return
            println(entireData.steps)
            val changedRecipe =
                ChangedRecipe(
                    title = entireData.title,
                    cookingTime = entireData.cookingTime,
                    thumbnail = entireData.thumbnail,
                    difficulty = entireData.difficulty,
                    description = entireData.introduction,
                    categories = entireData.categories,
                    ingredients = entireData.ingredients,
                    recipeSteps =
                        entireData.steps.map {
                            RecipeStep(
                                stepId = it.stepId,
                                description = it.description,
                                image =
                                    it.image.split("/").lastOrNull() ?: run {
                                        _uiEvent.value = Event(EditRecipeEvent.RecipePostFailure)
                                        return
                                    },
                                sequence = it.sequence,
                                recipeId = it.recipeId,
                                cookingTime = it.cookingTime,
                            )
                        },
                )
            val currentStepImages = entireData.steps

            currentStepImages.forEach {
                if (!it.imageUploaded || it.cookingTime.isEmpty() || it.description.isEmpty()) {
                    _uiEvent.value = Event(EditRecipeEvent.DescriptionFormNotCompleted)
                    _isMakingStepButtonClicked.value = true
                    return
                }
            }

            _isLoading.value = true

            viewModelScope.launch {
                feedRepository
                    .updateRecipe(recipeId, changedRecipe)
                    .onSuccess {
                        _isLoading.value = false
                        EditRecipeRepository.clearSavedRecipeData()
                        _uiEvent.value = Event(EditRecipeEvent.RecipePostSuccessful)
                    }.onFailure {
                        _isLoading.value = false
                        _uiEvent.value = Event(EditRecipeEvent.RecipePostFailure)
                    }
            }
        }

        private fun validateDescriptionForm(): Boolean =
            !categoryContent.value.isNullOrBlank() &&
                !introductionContent.value.isNullOrBlank() &&
                difficultySelectedValue.value != null &&
                !ingredientContent.value.isNullOrBlank() &&
                !titleContent.value.isNullOrBlank() &&
                !thumbnailTitle.isNullOrBlank() &&
                hourContent.value != null &&
                minuteContent.value != null &&
                secondContent.value != null

        private fun recipeCreation(): RecipeCreation? =
            EditRecipeRepository.fetchAllSavedRecipeData().getOrNull()?.let { recipeData ->
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

        private fun restoreDescriptionContents(existingRecipe: RecipeDescription) {
            with(existingRecipe) {
                titleContent.value = title
                ingredientContent.value = ingredients.joinToString(SEPARATOR_INGREDIENTS)
                difficultySelectedValue.value = difficulty.toFloat() / 2
                introductionContent.value = description
                val timeParts = cookingTime.split(SEPARATOR_TIME)
                hourContent.value = if (timeParts[0] == "00") "" else timeParts[0]
                minuteContent.value = if (timeParts[1] == "00") "" else timeParts[1]
                secondContent.value = if (timeParts[2] == "00") "" else timeParts[2]
                thumbnailTitle = thumbnail
                categoryContent.value = categories.joinToString()
                _thumbnailUri.value = Uri.parse(imageUri)
            }
        }

        private fun saveRecipeDescription() {
            val recipeDescription =
                RecipeDescription(
                    recipeDescriptionId = recipeId,
                    categories =
                        categoryContent.value
                            ?.split(SEPARATOR_INGREDIENTS)
                            ?.filter { it.trim().isNotEmpty() || it.trim().isNotBlank() } ?: emptyList(),
                    cookingTime = formatTimeRequired(),
                    description = introductionContent.value ?: "",
                    difficulty = (difficultySelectedValue.value?.times(2))?.toInt() ?: 0,
                    ingredients =
                        ingredientContent.value
                            ?.split(SEPARATOR_INGREDIENTS)
                            ?.filter { it.trim().isNotEmpty() || it.trim().isNotBlank() } ?: emptyList(),
                    thumbnail = thumbnailTitle ?: "",
                    title = titleContent.value ?: "",
                    imageUri = thumbnailUri.value.toString(),
                )

            val recipeCreation =
                RecipeCreation(
                    recipeId = recipeId,
                    title = recipeDescription.title,
                    introduction = recipeDescription.description,
                    cookingTime = recipeDescription.cookingTime,
                    difficulty = recipeDescription.difficulty,
                    ingredients =
                        recipeDescription.ingredients.map {
                            Ingredient(
                                ingredientId = 1L,
                                ingredientName = it,
                                requirement = "REQUIRED",
                            )
                        },
                    categories = recipeDescription.categories,
                    thumbnail = recipeDescription.thumbnail,
                    steps =
                        currentStepImages.value?.map {
                            RecipeStepMaking(
                                stepId = it.itemId.toLong(),
                                recipeId = recipeId,
                                description = it.description,
                                image = it.imageTitle,
                                sequence = it.sequence,
                                imageUri = it.uri.toString(),
                                cookingTime = it.cookingTime,
                                imageUploaded = it.uploaded,
                            )
                        } ?: emptyList(),
                )

            EditRecipeRepository.saveRecipeDescription(recipeCreation)
        }

        private fun saveRecipeSteps(recipeId: Long) {
        /* currentStepImages.value?.forEachIndexed { index, image ->
             stepMakingRepository
                 .saveRecipeStep(
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
         }*/
            val tmp =
                currentStepImages.value?.mapIndexed { index, it ->
                    RecipeStepMaking(
                        stepId = 1L,
                        recipeId = recipeId,
                        description = it.description,
                        image = it.imageTitle,
                        sequence = index + 1,
                        imageUri = it.uri.toString(),
                        cookingTime = it.cookingTime,
                        imageUploaded = it.uploaded,
                    )
                }
            println("tmp$tmp")
            EditRecipeRepository.saveRecipeSteps(
                tmp ?: emptyList(),
            )
        }

        private fun formatTimeRequired(): String =
            FORMAT_TIME_REQUIRED.format(
                hourContent.value?.toIntOrNull() ?: 0,
                minuteContent.value?.toIntOrNull() ?: 0,
                secondContent.value?.toIntOrNull() ?: 0,
            )

        override fun onAddImage() {
            _uiEvent.value = Event(EditRecipeEvent.AddThumbnailImage)
        }

        override fun onAddStepImages() {
            _uiEvent.value = Event(EditRecipeEvent.AddStepImages)
        }

        override fun onImageChange(id: Int) {
            _uiEvent.value = Event(EditRecipeEvent.ChangeImage(id))
        }

        override fun onDelete(id: Int) {
            _currentStepImages.value = currentStepImages.value?.filter { it.itemId != id }
            println("currentStepImages.value${currentStepImages.value}")

            saveRecipeSteps(recipeId)
            _uiEvent.value = Event(EditRecipeEvent.ImageDeletionSuccessful(id))
        }

        override fun onOrderChange(items: List<RecipeStepImage>) {
            _currentStepImages.value = items
        }

        override fun onStepImageClick(item: RecipeStepImage) {
            saveRecipeDescription()
            saveRecipeSteps(recipeId)
            println("currentStepImages.value${currentStepImages.value}")
            println(currentStepImages.value?.indexOf(item)?.plus(1))
            _uiEvent.value =
                Event(
                    EditRecipeEvent.NavigateToEditStep(
                        // sequence = currentStepImages.value?.indexOf(item)?.plus(1) ?: return,
                        sequence = item.sequence,
                    ),
                )
        }

        override fun onNavigateBack() {
            _uiEvent.value = Event(EditRecipeEvent.EditCancellation)
        }

        companion object {
            fun provideFactory(
                assistedFactory: EditRecipeViewModelFactory,
                recipeId: Long,
            ) = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = assistedFactory.create(recipeId) as T
            }

            private const val SEPARATOR_INGREDIENTS = ","
            private const val SEPARATOR_TIME = ":"
            private const val FORMAT_TIME_REQUIRED = "%02d:%02d:%02d"
            private const val EXCEPTION_URL_FILE_SIZE_UNMATCHED =
                "The sizes of files and urls should be the same."
        }
    }
