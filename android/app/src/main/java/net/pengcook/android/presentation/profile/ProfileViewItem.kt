package net.pengcook.android.presentation.profile

import net.pengcook.android.presentation.core.model.Recipe

sealed class ProfileViewItem(open val viewType: Int) {
    class ProfileDescription(
        val profile: Profile,
        override val viewType: Int = VIEW_TYPE_PROFILE_DESC,
    ) : ProfileViewItem(viewType)

    class ProfileButtons(
        override val viewType: Int = VIEW_TYPE_PROFILE_BUTTONS,
    ) : ProfileViewItem(viewType)

    data class ProfileFeeds(
        val recipe: Recipe,
        override val viewType: Int = VIEW_TYPE_FEEDS,
    ) : ProfileViewItem(viewType)

    companion object {
        const val VIEW_TYPE_PROFILE_DESC = 0
        const val VIEW_TYPE_PROFILE_BUTTONS = 1
        const val VIEW_TYPE_FEEDS = 2
    }
}
