package net.pengcook.android.presentation.signup

import dagger.assisted.AssistedFactory

@AssistedFactory
interface SignUpViewModelFactory {
    fun create(platformName: String): SignUpViewModel
}
