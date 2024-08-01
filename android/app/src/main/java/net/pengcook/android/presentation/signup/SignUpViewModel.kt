package net.pengcook.android.presentation.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.TokenRepository
import net.pengcook.android.domain.model.auth.Platform
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserSignUpForm
import net.pengcook.android.domain.usecase.ValidateNicknameUseCase
import net.pengcook.android.domain.usecase.ValidateUsernameUseCase
import net.pengcook.android.presentation.core.listener.SpinnerItemChangeListener
import net.pengcook.android.presentation.core.util.Event

class SignUpViewModel(
    private val platformName: String,
    private val authorizationRepository: AuthorizationRepository,
    private val tokenRepository: TokenRepository,
    private val validateUsernameUseCase: ValidateUsernameUseCase = ValidateUsernameUseCase(),
    private val validateNicknameUseCase: ValidateNicknameUseCase = ValidateNicknameUseCase(),
) : ViewModel(),
    BottomButtonClickListener,
    SpinnerItemChangeListener {
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
            val country = country.value ?: return@launch

            if (username == null || !validateUsernameUseCase(username)) {
                _signUpEvent.value = Event(SignUpEvent.UsernameInvalid)
                return@launch
            }

            if (nickname == null || !validateNicknameUseCase(nickname)) {
                _signUpEvent.value = Event(SignUpEvent.NicknameLengthInvalid)
                return@launch
            }

            authorizationRepository.fetchUsernameDuplication(username)
                .onSuccess { available ->
                    if (!available) {
                        _signUpEvent.value = Event(SignUpEvent.NicknameDuplicated)
                        return@launch
                    }
                }.onFailure {
                    _signUpEvent.value = Event(SignUpEvent.Error)
                }

            _isLoading.value = true

            val platformToken =
                tokenRepository.authorizationData.first().platformToken ?: return@launch

            authorizationRepository.signUp(
                platformName,
                UserSignUpForm(platformToken, country, nickname, username),
            ).onSuccess { signUpResult ->
                onSignUpSuccessful(signUpResult)
            }.onFailure {
                onSignUpFailure()
            }
        }
    }

    override fun onSelectionChange(item: String) {
        country.value = item
    }

    private suspend fun onSignUpSuccessful(signUpResult: SignUp) {
        _isLoading.value = false
        tokenRepository.updateAccessToken(signUpResult.accessToken)
        tokenRepository.updateRefreshToken(signUpResult.refreshToken)
        tokenRepository.updateCurrentPlatform(Platform.find(platformName))
        _signUpEvent.value = Event(SignUpEvent.NavigateToMain)
    }

    private fun onSignUpFailure() {
        _isLoading.value = false
        _signUpEvent.value = Event(SignUpEvent.Error)
    }
}
