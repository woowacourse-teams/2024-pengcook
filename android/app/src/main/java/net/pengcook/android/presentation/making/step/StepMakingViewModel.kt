package net.pengcook.android.presentation.making.step

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class StepMakingViewModel(
    private val recipeId: Long,
    val maximumStep: Int = 15,
    private val recipeStepMakingRepository: RecipeStepMakingRepository,
    private val makingRecipeRepository: MakingRecipeRepository,
) : ViewModel(),
    StepMakingEventHandler,
    AppbarDoubleActionEventListener {
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

    private var thumbnailTitle: String? = null

    private val _uiEvent: MutableLiveData<Event<RecipeStepMakingEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<RecipeStepMakingEvent>>
        get() = _uiEvent

    val completed: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        completed.apply {
            addSource(imageUploaded) { completed.value = completed() }
            addSource(introductionContent) { completed.value = completed() }
        }
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

        if (introduction.isNullOrEmpty()) {
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
        if (imageUploaded != true) {
            _uiEvent.value = Event(RecipeStepMakingEvent.ImageNotUploaded)
            return
        }

        if (introduction.isNullOrEmpty()) {
            _uiEvent.value = Event(RecipeStepMakingEvent.FormNotCompleted)
            return
        }
        if (stepNumber.value == 1) return
        viewModelScope.launch {
            saveStepData(stepNumber.value!!, StepAction.PREVIOUS)
        }
    }

    override fun navigationAction() {
        _uiEvent.value = Event(RecipeStepMakingEvent.NavigateBackToDescription)
    }

    override fun customAction() {
        viewModelScope.launch {
            if (imageUploaded.value != true) {
                _uiEvent.value = Event(RecipeStepMakingEvent.ImageNotUploaded)
                return@launch
            }

            if (introductionContent.value.isNullOrEmpty()) {
                _uiEvent.value = Event(RecipeStepMakingEvent.FormNotCompleted)
                return@launch
            }

            _isLoading.value = true
            saveStepData(stepNumber.value!!, StepAction.COMPLETE)

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
            _imageUri.value = null
            fetchRecipeStep(stepNumber)
            _isLoading.value = false
        }
    }

    private fun completed(): Boolean {
        return imageUploaded.value == true &&
            !introductionContent.value.isNullOrEmpty()
    }

    private fun changePage(stepAction: StepAction) {
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
        _imageUploaded.value = false
        introductionContent.value = ""
        thumbnailTitle = null
        completed.value = false
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
            introductionContent.value = recipeStep.description
            _imageUri.value = Uri.parse(recipeStep.imageUri)
            thumbnailTitle = recipeStep.image
            _imageUploaded.value = thumbnailTitle != null
        }.onFailure {
            introductionContent.value = ""
            _imageUri.value = null
            thumbnailTitle = null
            _imageUploaded.value = false
        }
    }

    private suspend fun saveStepData(
        stepNumber: Int,
        stepAction: StepAction,
    ) {
        val recipeStep =
            RecipeStepMaking(
                recipeId = recipeId,
                sequence = stepNumber,
                description = introductionContent.value ?: return,
                image = thumbnailTitle ?: return,
                stepId = 1L,
                imageUri = imageUri.value?.toString() ?: return,
            )

        recipeStepMakingRepository.saveRecipeStep(recipeId = recipeId, recipeStep = recipeStep)
            .onSuccess {
                changePage(stepAction)
            }.onFailure {
                _uiEvent.value = Event(RecipeStepMakingEvent.RecipePostFailure)
            }
    }
}
