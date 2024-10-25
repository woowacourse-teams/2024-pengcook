package net.pengcook.android.presentation.edit.step

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.data.repository.edit.EditRecipeRepository
import net.pengcook.android.presentation.core.listener.AppbarDoubleActionEventListener
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import net.pengcook.android.presentation.making.newstep.OnStepDataChangeListener

class EditStepsViewModel :
    ViewModel(),
    AppbarDoubleActionEventListener,
    OnStepDataChangeListener {
    private val _steps: MutableLiveData<MutableList<RecipeStepMaking>> = MutableLiveData()
    val steps: LiveData<MutableList<RecipeStepMaking>>
        get() = _steps

    private val _uiEvent = MutableLiveData<EditStepsEvent>()
    val uiEvent: LiveData<EditStepsEvent>
        get() = _uiEvent

    init {
        fetchRecipeSteps()
    }

    fun fetchRecipeSteps() {
        val response = EditRecipeRepository.fetchAllSavedRecipeData()
        println("response: $response")
        response.onSuccess { recipeCreation ->
            println("recipeCreation: $recipeCreation")
            _steps.value = recipeCreation.steps as MutableList<RecipeStepMaking>
            println("steps: ${_steps.value}")
            _uiEvent.value = EditStepsEvent.OnFetchComplete
        }
    }

    fun saveData() {
        val data = steps.value
        println(data)

        if (data == null) {
            _uiEvent.value = EditStepsEvent.OnSaveFailure
            return
        }
        EditRecipeRepository.saveRecipeSteps(data)
    }

    fun exit() {
        val data = steps.value
        println(data)

        if (data == null) {
            _uiEvent.value = EditStepsEvent.OnSaveFailure
            return
        }

        EditRecipeRepository.saveRecipeSteps(data)
        _uiEvent.value = EditStepsEvent.ExitEvent
    }

    private fun saveRecipeSteps() {
        val data = steps.value

        if (data == null) {
            _uiEvent.value = EditStepsEvent.OnSaveFailure
            return
        }
        EditRecipeRepository.saveRecipeSteps(data)
    }

    override fun navigationAction() {
        exit()
        println("data : ${EditRecipeRepository.fetchAllSavedRecipeData().getOrNull()}")

        _uiEvent.value = EditStepsEvent.NavigationEvent
    }

    override fun customAction() {
        saveData()
        println("data : ${EditRecipeRepository.fetchAllSavedRecipeData().getOrNull()}")

        _uiEvent.value = EditStepsEvent.TempSaveEvent
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
        private const val TIME_FORMAT = "%02d"
    }
}
