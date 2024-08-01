package net.pengcook.android.presentation.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.TokenRepository
import net.pengcook.android.domain.model.auth.Platform
import net.pengcook.android.presentation.core.util.Event

class OnboardingViewModel(
    private val authorizationRepository: AuthorizationRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _uiEvent: MutableLiveData<Event<OnboardingUiEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<OnboardingUiEvent>>
        get() = _uiEvent

    fun signIn(
        platformName: String,
        platformToken: String,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            signInWithServer(platformName, platformToken)
        }
    }

    private suspend fun signInWithServer(
        platformName: String,
        platformToken: String,
    ) {
        authorizationRepository.signIn(platformName, platformToken)
            .onSuccess { signInData ->
                tokenRepository.updateCurrentPlatform(Platform.find(platformName))
                tokenRepository.updatePlatformToken(platformToken)
                if (signInData.registered) {
                    tokenRepository.updateCurrentPlatform(Platform.find(platformName))
                    tokenRepository.updateAccessToken(signInData.accessToken)
                    _isLoading.value = false
                    _uiEvent.value = Event(OnboardingUiEvent.NavigateToMain)
                    return@onSuccess
                }
                _isLoading.value = false
                _uiEvent.value = Event(OnboardingUiEvent.NavigateToSignUp(platformName))
            }.onFailure {
                _isLoading.value = false
                _uiEvent.value = Event(OnboardingUiEvent.Error)
            }
    }
}
