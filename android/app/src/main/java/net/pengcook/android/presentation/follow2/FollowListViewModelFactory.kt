package net.pengcook.android.presentation.follow2

import dagger.assisted.AssistedFactory

@AssistedFactory
interface FollowListViewModelFactory {
    fun create(userId: Long): FollowListViewModel
}
