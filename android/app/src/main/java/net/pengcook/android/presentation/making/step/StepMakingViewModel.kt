package net.pengcook.android.presentation.making.step

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.presentation.core.listener.AppbarDoubleActionEventListener
import net.pengcook.android.presentation.core.model.RecipeStep
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.step.listener.StepMakingEventHandler

class StepMakingViewModel(
    private val recipeId: Long,
    private val maximumStep: Int = 15,
    private val recipeStepMakingRepository: RecipeStepMakingRepository,
) : ViewModel(),
    StepMakingEventHandler,
    AppbarDoubleActionEventListener {
    private val _maxStepPage = MutableLiveData<Int>(maximumStep)
    val maxStepPage: LiveData<Int>
        get() = _maxStepPage

    private val _stepNumber = MutableLiveData<Int>(1)
    val stepNumber: LiveData<Int> get() = _stepNumber

    val introductionContent = MutableLiveData<String>()

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl

    private val _fetchedRecipeStep = MutableLiveData<RecipeStep?>()
    val fetchedRecipeStep: LiveData<RecipeStep?>
        get() = _fetchedRecipeStep

    private val isIntroductionContentEmpty = MutableLiveData<Boolean>(true)

    private var _emptyIntroductionState = MutableLiveData<Event<Boolean>>()
    val emptyIntroductionState: LiveData<Event<Boolean>>
        get() = _emptyIntroductionState

    private var _isUploadingImage = MutableLiveData<Boolean>(false)
    val isUploadingImage: LiveData<Boolean>
        get() = _isUploadingImage

    private var _uploadingImageState = MutableLiveData<Event<Boolean>>()
    val uploadingImageState: LiveData<Event<Boolean>>
        get() = _uploadingImageState

    private var _completeStepMakingState = MutableLiveData<Event<Boolean>>()
    val completeStepMakingState: LiveData<Event<Boolean>>
        get() = _completeStepMakingState

    private var _quitStepMakingState = MutableLiveData<Event<Boolean>>()
    val quitStepMakingState: LiveData<Event<Boolean>>
        get() = _quitStepMakingState

    private var _uploadErrorState = MutableLiveData<Event<Boolean>>()
    val uploadErrorState: LiveData<Event<Boolean>>
        get() = _uploadErrorState

    init {
        initStepData(stepNumber.value!!)
    }

    override fun validateNextPageableCondition() {
        // Check if the introduction content is empty or Uploading image
        isIntroductionContentEmpty.value = introductionContent.value.isNullOrBlank()

        if (isIntroductionContentEmpty.value == true) {
            _emptyIntroductionState.value = Event(true)
        } else if (isUploadingImage.value == true) {
            _uploadingImageState.value = Event(true)
        } else {
            if (stepNumber.value == maximumStep) return
            uploadStepData(stepNumber.value!!)
            _stepNumber.value = _stepNumber.value?.plus(1)
            initStepData(stepNumber.value!!)
            isIntroductionContentEmpty.value = false
        }
    }

    override fun validatePreviousPageableCondition() {
        if (stepNumber.value == 1) return
        uploadStepData(stepNumber.value!!)
        _stepNumber.value = _stepNumber.value?.minus(1)
        initStepData(stepNumber.value!!)
    }

    override fun navigationAction() {
        _quitStepMakingState.value = Event(true)
    }

    override fun customAction() {
        if (introductionContent.value.isNullOrEmpty()) {
            _emptyIntroductionState.value = Event(true)
            return
        }
        viewModelScope.launch {
            uploadStepData(stepNumber.value!!)
        }
        _completeStepMakingState.value = Event(true)
    }

    private fun initStepData(stepNumber: Int) {
        viewModelScope.launch {
            val data = fetchStepData(stepNumber)

            if (data == null) {
                introductionContent.value = ""
                _imageUrl.value = ""
            } else {
                introductionContent.value = data.description
                _imageUrl.value = data.image
            }
        }
    }

    private suspend fun fetchStepData(stepNumber: Int): RecipeStep? {
        // Fetch step data from repository
        val result =
            recipeStepMakingRepository.fetchRecipeStep(
                recipeId = recipeId,
                sequence = stepNumber,
            )

        _fetchedRecipeStep.value = result.getOrNull()
        return result.getOrNull()
    }

    private fun uploadStepData(stepNumber: Int) {
        // Upload step data to repository
        viewModelScope.launch {
            recipeStepMakingRepository
                .uploadRecipeStep(
                    recipeId = recipeId,
                    recipeStep =
                        RecipeStep(
                            recipeId = recipeId,
                            sequence = stepNumber,
                            description = introductionContent.value ?: "",
                            image = imageUrl.value ?: "",
                            stepId = 1L,
                        ),
                ).onSuccess {
                }.onFailure {
                    _uploadErrorState.value = Event(true)
                }
        }
    }
}
