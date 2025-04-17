package net.pengcook.android.presentation.making.newstep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.presentation.core.listener.AppbarDoubleActionEventListener
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import javax.inject.Inject

@HiltViewModel
class NewStepMakingViewModel
    @Inject
    constructor(
        private val recipeStepMakingRepository: RecipeStepMakingRepository,
    ) : ViewModel(),
        AppbarDoubleActionEventListener,
        OnStepDataChangeListener {
        private var _recipeId: Long = 0L
        val recipeId: Long
            get() = _recipeId

        private val _steps: MutableLiveData<MutableList<RecipeStepMaking>> = MutableLiveData()
        val steps: LiveData<MutableList<RecipeStepMaking>>
            get() = _steps

        private val _newStepMakingEvent = MutableLiveData<NewStepMakingEvent>()
        val newStepMakingEvent: LiveData<NewStepMakingEvent>
            get() = _newStepMakingEvent

        fun fetchRecipeSteps() {
            viewModelScope.launch {
                val response = recipeStepMakingRepository.fetchRecipeSteps()
                response.onSuccess { recipeSteps ->
                    _steps.value = (recipeSteps ?: emptyList()) as MutableList<RecipeStepMaking>?
                    _recipeId = recipeSteps?.get(0)?.recipeId ?: 0L
                    _newStepMakingEvent.value = NewStepMakingEvent.OnFetchComplete
                }
            }
        }

        fun saveData() {
            viewModelScope.launch {
                saveRecipeSteps(recipeId)
            }
        }

        fun exit() {
            viewModelScope.launch {
                saveRecipeSteps(recipeId)
                _newStepMakingEvent.value = NewStepMakingEvent.ExitEvent
            }
        }

        private suspend fun saveRecipeSteps(recipeId: Long) {
            steps.value?.forEachIndexed { index, _ ->
                recipeStepMakingRepository.saveRecipeStep(
                    recipeId,
                    steps.value!![index],
                )
            }
        }

        override fun navigationAction() {
            _newStepMakingEvent.value = NewStepMakingEvent.NavigationEvent
        }

        override fun customAction() {
            _newStepMakingEvent.value = NewStepMakingEvent.TempSaveEvent
            viewModelScope.launch {
                saveRecipeSteps(steps.value!![0].recipeId)
            }
        }

        override fun onDescriptionChanged(
            sequence: Int,
            description: String,
        ) {
            val changedValue =
                _steps.value?.get(sequence)?.copy(description = description) ?: RecipeStepMaking.EMPTY
            _steps.value?.set(sequence, changedValue)
        }

        override fun onMinuteChanged(
            sequence: Int,
            minute: String,
        ) {
            val currentMinute = if (minute.isEmpty()) 0 else minute.toInt()
            val currentMinuteString = String.format(TIME_FORMAT, currentMinute)
            val currentTime = _steps.value?.get(sequence)?.cookingTime ?: "00:00:00"
            val currentHour = currentTime.split(":")[0]
            val currentSecond = currentTime.split(":")[2]
            val changedCookingTime = "$currentHour:$currentMinuteString:$currentSecond"
            val changedValue =
                _steps.value?.get(sequence)?.copy(cookingTime = changedCookingTime)
                    ?: RecipeStepMaking.EMPTY
            _steps.value?.set(sequence, changedValue)
        }

        override fun onSecondChanged(
            sequence: Int,
            second: String,
        ) {
            val currentSecond = if (second.isEmpty()) 0 else second.toInt()
            val currentSecondString = String.format(TIME_FORMAT, currentSecond)
            val currentTime = _steps.value?.get(sequence)?.cookingTime ?: "00:00:00"
            val currentHour = currentTime.split(":")[0]
            val currentMinute = currentTime.split(":")[1]
            val changedCookingTime = "$currentHour:$currentMinute:$currentSecondString"
            val changedValue =
                _steps.value?.get(sequence)?.copy(cookingTime = changedCookingTime)
                    ?: RecipeStepMaking.EMPTY
            _steps.value?.set(sequence, changedValue)
        }

        companion object {
            fun provideFactory(assistedFactory: NewStepMakingViewModelFactory): ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return assistedFactory.create() as T
                    }
                }

            private const val TIME_FORMAT = "%02d"
        }
    }

sealed interface NewStepMakingEvent {
    data object NavigationEvent : NewStepMakingEvent

    data object TempSaveEvent : NewStepMakingEvent

    data object OnFetchComplete : NewStepMakingEvent

    data object ExitEvent : NewStepMakingEvent
}
