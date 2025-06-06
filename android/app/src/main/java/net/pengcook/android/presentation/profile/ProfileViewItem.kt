package net.pengcook.android.presentation.profile

import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.presentation.core.model.RecipeForList

sealed class ProfileViewItem(open val viewType: Int) {
    class ProfileDescription(
        val profile: UserProfile,
        override val viewType: Int = VIEW_TYPE_PROFILE_DESC,
    ) : ProfileViewItem(viewType)

    class ProfileButtons(
        override val viewType: Int = VIEW_TYPE_PROFILE_BUTTONS,
    ) : ProfileViewItem(viewType)

    data class ProfileFeeds(
        val recipe: RecipeForList,
        override val viewType: Int = VIEW_TYPE_FEEDS,
    ) : ProfileViewItem(viewType)

    companion object {
        const val VIEW_TYPE_PROFILE_DESC = 0
        const val VIEW_TYPE_PROFILE_BUTTONS = 1
        const val VIEW_TYPE_FEEDS = 2
    }
}
