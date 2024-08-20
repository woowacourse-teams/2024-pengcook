package net.pengcook.android.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.photo.ImageRepository
import net.pengcook.android.data.repository.profile.ProfileRepository
import net.pengcook.android.domain.usecase.ValidateNicknameUseCase
import net.pengcook.android.domain.usecase.ValidateUsernameUseCase

class EditProfileViewModelFactory(
    private val authorizationRepository: AuthorizationRepository,
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
    private val validateUsernameUseCase: ValidateUsernameUseCase = ValidateUsernameUseCase(),
    private val validateNicknameUseCase: ValidateNicknameUseCase = ValidateNicknameUseCase(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(
                authorizationRepository = authorizationRepository,
                imageRepository = imageRepository,
                profileRepository = profileRepository,
                validateUsernameUseCase = validateUsernameUseCase,
                validateNicknameUseCase = validateNicknameUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
