package net.pengcook.android.presentation.profile

import dagger.assisted.AssistedFactory

@AssistedFactory
interface ProfilePagingSourceFactory {
    fun create(
        initialPageNumber: Int,
        profileFeedType: ProfileFeedType,
    ): ProfilePagingSource
}
