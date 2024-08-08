package net.pengcook.android.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfileViewModelFactory(
    private val profilePagingSource: ProfilePagingSource,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(profilePagingSource) as T
        }
        throw IllegalArgumentException()
    }
}
