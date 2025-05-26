package net.pengcook.android.presentation.profile

import net.pengcook.android.presentation.core.model.RecipeForList

interface ProfileFeedClickListener {
    fun onClick(recipe: RecipeForList)
}
