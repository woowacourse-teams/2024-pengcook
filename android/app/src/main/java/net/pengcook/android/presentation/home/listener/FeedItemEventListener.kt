package net.pengcook.android.presentation.home.listener

import net.pengcook.android.presentation.core.model.RecipeForList

interface FeedItemEventListener {
    fun onNavigateToDetail(recipe: RecipeForList)
}
