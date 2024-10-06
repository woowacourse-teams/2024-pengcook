package net.pengcook.android.presentation.category

sealed interface CategoryUiEvent {
    data class NavigateToList(val categoryCode: String) : CategoryUiEvent
}
