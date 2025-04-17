package net.pengcook.android.presentation.follow

import dagger.assisted.AssistedFactory

@AssistedFactory
interface FollowListViewModelFactory {
    fun create(userId: Long): FollowListViewModel
}
