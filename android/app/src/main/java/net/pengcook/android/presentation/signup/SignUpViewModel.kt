package net.pengcook.android.presentation.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.data.repository.photo.ImageRepository
import net.pengcook.android.domain.model.auth.Platform
import net.pengcook.android.domain.model.auth.SignUp
import net.pengcook.android.domain.model.auth.UserSignUpForm
import net.pengcook.android.domain.usecase.ValidateNicknameUseCase
import net.pengcook.android.domain.usecase.ValidateUsernameUseCase
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.listener.SpinnerItemChangeListener
import net.pengcook.android.presentation.core.util.Event
import java.io.File

class SignUpViewModel @AssistedInject constructor(
    @Assisted private val platformName: String,
    private val authorizationRepository: AuthorizationRepository,
    private val sessionRepository: SessionRepository,
    private val imageRepository: ImageRepository,
    private val validateUsernameUseCase: ValidateUsernameUseCase,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : ViewModel(),
    BottomButtonClickListener,
    SpinnerItemChangeListener,
    AppbarSingleActionEventListener {
    val usernameContent: MutableLiveData<String> = MutableLiveData()
    val nicknameContent: MutableLiveData<String> = MutableLiveData()
    val country: MutableLiveData<String> = MutableLiveData()

    private val _imageUri: MutableLiveData<Uri> = MutableLiveData()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    private val _imageUploaded: MutableLiveData<Boolean> = MutableLiveData(false)
    val imageUploaded: LiveData<Boolean>
        get() = _imageUploaded

    private val _imageSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val imageSelected: LiveData<Boolean>
        get() = _imageSelected

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _signUpEvent: MutableLiveData<Event<SignUpEvent>> = MutableLiveData()
    val signUpEvent: LiveData<Event<SignUpEvent>>
        get() = _signUpEvent

    private var imageTitle: String? = null

    fun fetchImageUri(keyName: String) {
        viewModelScope.launch {
            _imageSelected.value = true
            imageRepository.fetchImageUri(keyName)
                .onSuccess { uri ->
                    _signUpEvent.value = Event(SignUpEvent.PresignedUrlRequestSuccessful(uri))
                }.onFailure {
                    _signUpEvent.value = Event(SignUpEvent.PostImageFailure)
                }
        }
    }

    fun uploadImageToS3(
        presignedUrl: String,
        file: File,
    ) {
        viewModelScope.launch {
            imageRepository.uploadImage(presignedUrl, file)
                .onSuccess {
                    imageTitle = file.name
                    _signUpEvent.value = Event(SignUpEvent.PostImageSuccessful)
                    _imageUploaded.value = true
                }.onFailure {
                    _signUpEvent.value = Event(SignUpEvent.PostImageFailure)
                }
        }
    }

    fun changeCurrentImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun addImage() {
        _signUpEvent.value = Event(SignUpEvent.AddImage)
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

    private suspend fun signUp(
        platformToken: String,
        country: String,
        nickname: String,
        username: String,
    ) {
        authorizationRepository
            .signUp(
                platformName,
                UserSignUpForm(platformToken, country, nickname, username, imageTitle),
            ).onSuccess { signUpResult ->
                onSignUpSuccessful(signUpResult)
            }.onFailure {
                onSignUpFailure()
            }
    }

    private suspend fun usernameAvailable(username: String): Boolean {
        authorizationRepository
            .checkUsernameDuplication(username)
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

    companion object {
        fun provideFactory(
            assistedFactory: SignUpViewModelFactory,
            platformName: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return assistedFactory.create(platformName) as T
            }
        }
    }
}
