package net.pengcook.android.presentation.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.core.util.Event

class CategoryViewModel : ViewModel(), CategoryEventListener {
    private val _uiEvent: MutableLiveData<Event<CategoryUiEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<CategoryUiEvent>>
        get() = _uiEvent

    override fun onCategorySelect(categoryCode: String) {
        _uiEvent.value = Event(CategoryUiEvent.NavigateToList(categoryCode))
    }
}
