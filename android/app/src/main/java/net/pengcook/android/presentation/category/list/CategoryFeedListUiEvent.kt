package net.pengcook.android.presentation.category.list

import net.pengcook.android.presentation.core.model.RecipeForList

sealed interface CategoryFeedListUiEvent {
    data object NavigateBack : CategoryFeedListUiEvent

    data class NavigateToDetail(val recipe: RecipeForList) : CategoryFeedListUiEvent
}
