package net.pengcook.android.presentation.otherprofile

import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.presentation.core.model.RecipeForList

data class OtherProfileState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val isFollowing: Boolean = false,
    val recipes: List<RecipeForList> = emptyList(),
)
