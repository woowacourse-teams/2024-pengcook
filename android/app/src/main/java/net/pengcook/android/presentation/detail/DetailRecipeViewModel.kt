package net.pengcook.android.presentation.detail

// DetailRecipeViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.core.model.Recipe

class DetailRecipeViewModel(private val recipe: Recipe) : ViewModel() {
    private val _navigateToStepEvent = MutableLiveData<Boolean>()
    val navigateToStepEvent: LiveData<Boolean> get() = _navigateToStepEvent

    fun onNavigateToMakingStep() {
        _navigateToStepEvent.value = true
    }
}
