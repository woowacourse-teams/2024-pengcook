package net.pengcook.android.presentation.profile

sealed interface ProfileFeedType {
    data object MyFeed : ProfileFeedType

    class OthersFeed(val userId: Long) : ProfileFeedType
}
