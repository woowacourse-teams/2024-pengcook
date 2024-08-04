package net.pengcook.android.presentation.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.domain.model.auth.Platform
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserSignUpForm
import net.pengcook.android.domain.usecase.ValidateNicknameUseCase
import net.pengcook.android.domain.usecase.ValidateUsernameUseCase
import net.pengcook.android.presentation.core.listener.AppbarActionEventListener
import net.pengcook.android.presentation.core.listener.SpinnerItemChangeListener
import net.pengcook.android.presentation.core.util.Event

class SignUpViewModel(
    private val platformName: String,
    private val authorizationRepository: AuthorizationRepository,
    private val sessionRepository: SessionRepository,
    private val validateUsernameUseCase: ValidateUsernameUseCase = ValidateUsernameUseCase(),
    private val validateNicknameUseCase: ValidateNicknameUseCase = ValidateNicknameUseCase(),
) : ViewModel(),
    BottomButtonClickListener,
    SpinnerItemChangeListener,
    AppbarActionEventListener {
    val usernameContent: MutableLiveData<String> = MutableLiveData()
    val nicknameContent: MutableLiveData<String> = MutableLiveData()
    val country: MutableLiveData<String> = MutableLiveData()

    private val _imageUri: MutableLiveData<Uri> = MutableLiveData()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _signUpEvent: MutableLiveData<Event<SignUpEvent>> = MutableLiveData()
    val signUpEvent: LiveData<Event<SignUpEvent>>
        get() = _signUpEvent

    fun changeProfileImage(uri: Uri) {
        _imageUri.value = uri
    }

    override fun onConfirm() {
        viewModelScope.launch {
            val username = usernameContent.value
            val nickname = nicknameContent.value
            val country = country.value

            if (username.isNullOrEmpty() || nickname.isNullOrEmpty() || country.isNullOrEmpty()) {
                _signUpEvent.value = Event(SignUpEvent.SignUpFormNotCompleted)
                return@launch
            }
            if (!signUpFormValid(username, nickname)) return@launch
            if (!usernameAvailable(username)) return@launch

            val platformToken =
                sessionRepository.sessionData.first().platformToken ?: return@launch

            _isLoading.value = true
            signUp(platformToken, country, nickname, username)
        }
    }

    override fun onSelectionChange(item: String) {
        country.value = item
    }

    override fun onNavigateBack() {
        _signUpEvent.value = Event(SignUpEvent.BackPressed)
    }

    private suspend fun signUp(
        platformToken: String,
        country: String,
        nickname: String,
        username: String,
    ) {
        authorizationRepository.signUp(
            platformName,
            UserSignUpForm(platformToken, country, nickname, username),
        ).onSuccess { signUpResult ->
            onSignUpSuccessful(signUpResult)
        }.onFailure {
            onSignUpFailure()
        }
    }

    private suspend fun usernameAvailable(username: String): Boolean {
        authorizationRepository.checkUsernameDuplication(username)
            .onSuccess { available ->
                if (!available) {
                    _signUpEvent.value = Event(SignUpEvent.NicknameDuplicated)
                    return false
                }
            }.onFailure {
                _signUpEvent.value = Event(SignUpEvent.Error)
                return false
            }

        return true
    }

    private suspend fun onSignUpSuccessful(signUpResult: SignUp) {
        _isLoading.value = false
        sessionRepository.updateAccessToken(signUpResult.accessToken)
        sessionRepository.updateRefreshToken(signUpResult.refreshToken)
        sessionRepository.updateCurrentPlatform(Platform.find(platformName))
        _signUpEvent.value = Event(SignUpEvent.SignInSuccessful)
    }

    private fun onSignUpFailure() {
        _isLoading.value = false
        _signUpEvent.value = Event(SignUpEvent.Error)
    }

    private fun signUpFormValid(
        username: String,
        nickname: String,
    ): Boolean {
        if (!validateUsernameUseCase(username)) {
            _signUpEvent.value = Event(SignUpEvent.UsernameInvalid)
            return false
        }

        if (!validateNicknameUseCase(nickname)) {
            _signUpEvent.value = Event(SignUpEvent.NicknameLengthInvalid)
            return false
        }

        return true
    }
}
