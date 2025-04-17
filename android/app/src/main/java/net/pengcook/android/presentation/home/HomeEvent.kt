package net.pengcook.android.presentation.home

import net.pengcook.android.presentation.core.model.RecipeForList

sealed interface HomeEvent {
    data class NavigateToDetail(
        val recipe: RecipeForList,
    ) : HomeEvent

    data class NavigateToProfile(
        val recipe: RecipeForList,
    ) : HomeEvent
}
