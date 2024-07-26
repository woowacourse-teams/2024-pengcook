package net.pengcook.android.presentation.category.list

import net.pengcook.android.presentation.core.model.Recipe

sealed interface CategoryFeedListUiEvent {
    data object NavigateBack : CategoryFeedListUiEvent

    data class NavigateToDetail(val recipe: Recipe) : CategoryFeedListUiEvent
}
