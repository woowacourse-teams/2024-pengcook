package net.pengcook.android.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository

class OnboardingViewModelFactory(
    private val authorizationRepository: AuthorizationRepository,
    private val sessionRepository: SessionRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(
                authorizationRepository = authorizationRepository,
                sessionRepository = sessionRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
