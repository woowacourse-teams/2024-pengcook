package net.pengcook.android.presentation.home

import net.pengcook.android.presentation.core.model.RecipeForList

interface HomeEventListener {
    fun onNavigateToProfile(recipe: RecipeForList)
}
