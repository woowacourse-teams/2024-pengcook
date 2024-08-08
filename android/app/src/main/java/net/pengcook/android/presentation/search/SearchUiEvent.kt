package net.pengcook.android.presentation.search

import net.pengcook.android.presentation.core.model.Recipe

sealed interface SearchUiEvent {
    data class RecipeSelected(val recipe: Recipe) : SearchUiEvent

    data object SearchFailure : SearchUiEvent
}
