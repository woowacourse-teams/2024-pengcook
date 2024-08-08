package net.pengcook.android.presentation.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.domain.model.auth.Platform
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.signup.BottomButtonClickListener

class OnboardingViewModel(
    private val authorizationRepository: AuthorizationRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel(), BottomButtonClickListener {
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
            signInWithServer(platformName, platformToken)
        }
    }

    override fun onConfirm() {
        _isLoading.value = true
        _uiEvent.value = Event(OnboardingUiEvent.StartSignIn)
    }

    private suspend fun signInWithServer(
        platformName: String,
        platformToken: String,
    ) {
        authorizationRepository.signIn(platformName, platformToken)
            .onSuccess { signInData ->
                sessionRepository.updateCurrentPlatform(Platform.find(platformName))
                sessionRepository.updatePlatformToken(platformToken)
                if (signInData.registered) {
                    sessionRepository.updateCurrentPlatform(Platform.find(platformName))
                    sessionRepository.updateAccessToken(signInData.accessToken)
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
