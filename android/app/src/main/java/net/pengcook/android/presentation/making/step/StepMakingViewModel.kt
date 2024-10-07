package net.pengcook.android.presentation.making.step

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.domain.model.recipemaking.RecipeCreation
import net.pengcook.android.presentation.core.listener.AppbarDoubleActionEventListener
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.step.listener.StepMakingEventHandler
import java.io.File

class StepMakingViewModel
    @AssistedInject
    constructor(
        @Assisted private val recipeId: Long,
        @Assisted val maximumStep: Int = 15,
        private val recipeStepMakingRepository: RecipeStepMakingRepository,
        private val makingRecipeRepository: MakingRecipeRepository,
    ) : ViewModel(),
        StepMakingEventHandler,
        AppbarDoubleActionEventListener {
        private val stepTraversalStatus = MutableList(maximumStep) { false }

        private val _stepNumber = MutableLiveData<Int>(1)
        val stepNumber: LiveData<Int> get() = _stepNumber

        val introductionContent = MutableLiveData<String>()

        private val _imageUri = MutableLiveData<Uri?>()
        val imageUri: LiveData<Uri?>
            get() = _imageUri

        private var _isLoading = MutableLiveData<Boolean>(false)
        val isLoading: LiveData<Boolean>
            get() = _isLoading

        private val _imageUploaded: MutableLiveData<Boolean> = MutableLiveData(false)
        val imageUploaded: LiveData<Boolean>
            get() = _imageUploaded

        private val _imageSelected: MutableLiveData<Boolean> = MutableLiveData(false)
        val imageSelected: LiveData<Boolean>
            get() = _imageSelected

        private var thumbnailTitle: String? = null

        private val _uiEvent: MutableLiveData<Event<RecipeStepMakingEvent>> = MutableLiveData()
        val uiEvent: LiveData<Event<RecipeStepMakingEvent>>
            get() = _uiEvent

        private var completionPressed = false

        private val _errorVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
        val errorVisibility: LiveData<Boolean>
            get() = _errorVisibility

        private val stepCompletion: MutableMap<Int, Boolean> = mutableMapOf(1 to false)

        val minuteContent = MutableLiveData<String>()
        val secondContent = MutableLiveData<String>()

        init {
            val stepNumber = stepNumber.value
            if (stepNumber != null) {
                initStepData(stepNumber)
            }
        }

        override fun onAddImage() {
            _uiEvent.value = Event(RecipeStepMakingEvent.AddImage)
        }

        override fun moveToNextPage() {
            val imageUploaded = imageUploaded.value
            val introduction = introductionContent.value

            if (imageUploaded != true) {
                _uiEvent.value = Event(RecipeStepMakingEvent.ImageNotUploaded)
                return
            }

            if (stepNumber.value !in stepCompletion.keys && introduction.isNullOrEmpty()) {
                _uiEvent.value = Event(RecipeStepMakingEvent.FormNotCompleted)
                return
            }

            if (stepNumber.value == maximumStep) return

            viewModelScope.launch {
                saveStepData(stepNumber.value!!, StepAction.NEXT)
            }
        }

        override fun moveToPreviousPage() {
            val imageUploaded = imageUploaded.value
            val introduction = introductionContent.value
            if (imageUploaded != true && imageSelected.value == true) {
                _uiEvent.value = Event(RecipeStepMakingEvent.ImageNotUploaded)
                return
            }

            if (stepNumber.value == 1) return

            if (introduction.isNullOrEmpty() && imageSelected.value != true) {
                moveStep(StepAction.PREVIOUS)
                return
            }

            viewModelScope.launch {
                saveStepData(stepNumber.value!!, StepAction.PREVIOUS)
            }
        }

        override fun navigationAction() {
            _uiEvent.value = Event(RecipeStepMakingEvent.NavigateBackToDescription)
        }

        override fun customAction() {
            viewModelScope.launch {
                completionPressed = true
                _errorVisibility.value = true

                if (imageSelected.value != true) {
                    _uiEvent.value = Event(RecipeStepMakingEvent.FormNotCompleted)
                    return@launch
                }

                if (imageUploaded.value != true) {
                    _uiEvent.value = Event(RecipeStepMakingEvent.ImageNotUploaded)
                    return@launch
                }

                _isLoading.value = true
                saveStepData(stepNumber.value!!, StepAction.COMPLETE)

                val notCompleted = stepCompletion.values.any { !it }

                if (notCompleted) {
                    _isLoading.value = false
                    _uiEvent.value = Event(RecipeStepMakingEvent.FormNotCompleted)
                    return@launch
                }

                val recipeCreation = recipeCreation()
                if (recipeCreation == null) {
                    _isLoading.value = false
                    _uiEvent.value = Event(RecipeStepMakingEvent.RecipePostFailure)
                    return@launch
                }

                postRecipe(recipeCreation)
            }
        }

        fun changeCurrentImage(uri: Uri) {
            _imageUri.value = uri
        }

        fun fetchImageUri(keyName: String) {
            viewModelScope.launch {
                _imageSelected.value = true
                try {
                    val uri = makingRecipeRepository.fetchImageUri(keyName)
                    _uiEvent.value = Event(RecipeStepMakingEvent.PresignedUrlRequestSuccessful(uri))
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    _uiEvent.value = Event(RecipeStepMakingEvent.PostImageFailure)
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
                    _uiEvent.value = Event(RecipeStepMakingEvent.PostImageSuccessful)
                    _imageUploaded.value = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiEvent.value = Event(RecipeStepMakingEvent.PostImageFailure)
                }
            }
        }

        private fun initStepData(stepNumber: Int) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorVisibility.value = completionPressed && stepTraversalStatus[stepNumber - 1]
                stepTraversalStatus[stepNumber - 1] = true
                _imageUri.value = null
                fetchRecipeStep(stepNumber)
                _isLoading.value = false
            }
        }

        private fun moveStep(stepAction: StepAction) {
            when (stepAction) {
                StepAction.NEXT -> {
                    resetData()
                    _stepNumber.value = this.stepNumber.value?.plus(1)
                    initStepData(this.stepNumber.value!!)
                }

                StepAction.PREVIOUS -> {
                    resetData()
                    _stepNumber.value = this.stepNumber.value?.minus(1)
                    initStepData(this.stepNumber.value!!)
                }

                StepAction.COMPLETE -> Unit
            }
        }

        private fun resetData() {
            _imageUri.value = null
            _imageSelected.value = false
            _imageUploaded.value = false
            introductionContent.value = ""
            thumbnailTitle = null
            minuteContent.value = ""
            secondContent.value = ""
        }

        private suspend fun postRecipe(recipeCreation: RecipeCreation) {
            makingRecipeRepository.postNewRecipe(recipeCreation)
                .onSuccess {
                    _isLoading.value = false
                    makingRecipeRepository.deleteRecipeDescription(recipeId)
                    recipeStepMakingRepository.deleteRecipeSteps(recipeId)
                    _uiEvent.value = Event(RecipeStepMakingEvent.RecipePostSuccessful)
                }.onFailure {
                    _isLoading.value = false
                    _uiEvent.value = Event(RecipeStepMakingEvent.RecipePostFailure)
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

        private suspend fun fetchRecipeStep(stepNumber: Int) {
            recipeStepMakingRepository.fetchRecipeStep(
                recipeId = recipeId,
                sequence = stepNumber,
            ).onSuccess { recipeStep ->
                if (recipeStep == null) return@onSuccess
                val minute =
                    recipeStep.cookingTime.split(SEPARATOR_TIME).getOrNull(1) ?: DEFAULT_TIME_STRING
                val second =
                    recipeStep.cookingTime.split(SEPARATOR_TIME).getOrNull(2) ?: DEFAULT_TIME_STRING
                introductionContent.value = recipeStep.description
                minuteContent.value = if (minute.toIntOrNull() == 0) DEFAULT_TIME_STRING else minute
                secondContent.value = if (second.toIntOrNull() == 0) DEFAULT_TIME_STRING else second
                if (recipeStep.imageUri.isNotEmpty()) {
                    _imageUri.value = Uri.parse(recipeStep.imageUri)
                }
                thumbnailTitle = recipeStep.image
                _imageUploaded.value = thumbnailTitle != null
                _imageSelected.value = thumbnailTitle != null
            }.onFailure {
                introductionContent.value = ""
                _imageUri.value = null
                thumbnailTitle = null
                _imageUploaded.value = false
                _imageSelected.value = false
            }
        }

        private suspend fun saveStepData(
            stepNumber: Int,
            stepAction: StepAction,
        ) {
            val minute = minuteContent.value
            val second = secondContent.value

            val recipeStep =
                RecipeStepMaking(
                    recipeId = recipeId,
                    sequence = stepNumber,
                    description = introductionContent.value ?: DEFAULT_TIME_STRING,
                    image = thumbnailTitle ?: DEFAULT_TIME_STRING,
                    stepId = 1L,
                    imageUri = imageUri.value?.toString() ?: DEFAULT_IMAGE_URI,
                    cookingTime =
                        FORMAT_TIME_REQUIRED.format(
                            DEFAULT_TIME_VALUE,
                            minute?.toIntOrNull() ?: DEFAULT_TIME_VALUE,
                            second?.toIntOrNull() ?: DEFAULT_TIME_VALUE,
                        ),
                )

            recipeStepMakingRepository.saveRecipeStep(recipeId = recipeId, recipeStep = recipeStep)
                .onSuccess {
                    stepCompletion[stepNumber] =
                        recipeStep.imageUri.isNotEmpty() && recipeStep.description.isNotEmpty() && recipeStep.image.isNotEmpty()
                    moveStep(stepAction)
                }.onFailure {
                    _uiEvent.value = Event(RecipeStepMakingEvent.RecipePostFailure)
                }
        }

        companion object {
            private const val FORMAT_TIME_REQUIRED = "%02d:%02d:%02d"
            private const val SEPARATOR_TIME = ":"
            private const val DEFAULT_TIME_STRING = ""
            private const val DEFAULT_TIME_VALUE = 0
            private const val DEFAULT_IMAGE_URI = ""

            fun provideFactory(
                assistedFactory: StepMakingViewModelFactory,
                recipeId: Long,
                maximumStep: Int = 15,
            ): ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return assistedFactory.create(recipeId, maximumStep) as T
                    }
                }
        }
    }
