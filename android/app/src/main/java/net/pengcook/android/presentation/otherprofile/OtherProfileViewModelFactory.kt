package net.pengcook.android.presentation.otherprofile

import dagger.assisted.AssistedFactory

@AssistedFactory
interface OtherProfileViewModelFactory {
    fun create(userId: Long): OtherProfileViewModel
}
