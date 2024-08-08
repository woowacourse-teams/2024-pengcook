package net.pengcook.android.presentation.profile

import net.pengcook.android.presentation.core.model.Recipe

interface ProfileFeedClickListener {
    fun onClick(recipe: Recipe)
}
