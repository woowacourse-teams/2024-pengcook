package net.pengcook.android.presentation.detail

// DetailRecipeViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.util.Event

class DetailRecipeViewModel(
    private val recipe: Recipe,
) : ViewModel() {
    private val _navigateToStepEvent = MutableLiveData<Event<Boolean>>()
    val navigateToStepEvent: LiveData<Event<Boolean>> get() = _navigateToStepEvent

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    fun toggleFavorite() {
        _isFavorite.value = _isFavorite.value?.not()
    }

    fun onNavigateToMakingStep() {
        _navigateToStepEvent.value = Event(true)
    }
}
