package net.pengcook.android.presentation.making

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.listener.RecipeMakingEventListener

class RecipeMakingViewModel : ViewModel(), RecipeMakingEventListener {
    private val _uiEvent: MutableLiveData<Event<MakingEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<MakingEvent>>
        get() = _uiEvent

    val titleContent = MutableLiveData<String>()

    val categorySelectedValue = MutableLiveData<String>()

    val ingredientContent = MutableLiveData<String>()

    val difficultySelectedValue = MutableLiveData<String>()

    val introductionContent = MutableLiveData<String>()

    override fun onNavigateToStep() {
        _uiEvent.value = Event(MakingEvent.NavigateToStep)
    }
}

sealed interface MakingEvent {
    data object NavigateToStep : MakingEvent
}
