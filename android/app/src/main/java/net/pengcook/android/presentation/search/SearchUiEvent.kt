package net.pengcook.android.presentation.search

import net.pengcook.android.presentation.core.model.RecipeForList

sealed interface SearchUiEvent {
    data class RecipeSelected(val recipe: RecipeForList) : SearchUiEvent

    data object SearchFailure : SearchUiEvent
}
