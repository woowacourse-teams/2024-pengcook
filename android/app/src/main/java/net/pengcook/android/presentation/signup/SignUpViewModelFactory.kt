package net.pengcook.android.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.domain.usecase.ValidateNicknameUseCase
import net.pengcook.android.domain.usecase.ValidateUsernameUseCase

class SignUpViewModelFactory(
    private val platformName: String,
    private val authorizationRepository: AuthorizationRepository,
    private val sessionRepository: SessionRepository,
    private val validateUsernameUseCase: ValidateUsernameUseCase = ValidateUsernameUseCase(),
    private val validateNicknameUseCase: ValidateNicknameUseCase = ValidateNicknameUseCase(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(
                platformName = platformName,
                authorizationRepository = authorizationRepository,
                sessionRepository = sessionRepository,
                validateUsernameUseCase = validateUsernameUseCase,
                validateNicknameUseCase = validateNicknameUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}