package net.pengcook.android.presentation.setting.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.photo.ImageRepository
import net.pengcook.android.data.repository.profile.ProfileRepository
import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.domain.usecase.ValidateNicknameUseCase
import net.pengcook.android.domain.usecase.ValidateUsernameUseCase
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.listener.SpinnerItemChangeListener
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.signup.BottomButtonClickListener
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authorizationRepository: AuthorizationRepository,
    private val imageRepository: ImageRepository,
    private val profileRepository: ProfileRepository,
    private val validateUsernameUseCase: ValidateUsernameUseCase,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : ViewModel(),
    BottomButtonClickListener,
    SpinnerItemChangeListener,
    AppbarSingleActionEventListener {
    private lateinit var existingUsername: String

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

    private val _signUpEvent: MutableLiveData<Event<EditProfileEvent>> = MutableLiveData()
    val signUpEvent: LiveData<Event<EditProfileEvent>>
        get() = _signUpEvent

    private var imageTitle: String? = null

    init {
        fetchUserInformation()
    }

    fun fetchImageUri(keyName: String) {
        viewModelScope.launch {
            _imageSelected.value = true
            imageRepository.fetchImageUri(keyName)
                .onSuccess { uri ->
                    _signUpEvent.value = Event(EditProfileEvent.PresignedUrlRequestSuccessful(uri))
                }.onFailure {
                    _signUpEvent.value = Event(EditProfileEvent.PostImageFailure)
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
                    _signUpEvent.value = Event(EditProfileEvent.PostImageSuccessful)
                    _imageUploaded.value = true
                }.onFailure {
                    _signUpEvent.value = Event(EditProfileEvent.PostImageFailure)
                }
        }
    }

    fun changeCurrentImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun addImage() {
        _signUpEvent.value = Event(EditProfileEvent.AddImage)
    }

    override fun onConfirm() {
        viewModelScope.launch {
            val username = usernameContent.value
            val nickname = nicknameContent.value
            val country = country.value

            if (username.isNullOrEmpty() || nickname.isNullOrEmpty() || country.isNullOrEmpty()) {
                _signUpEvent.value = Event(EditProfileEvent.FormNotCompleted)
                return@launch
            }
            if (!validation(username, nickname)) return@launch
            if (!usernameAvailable(username)) return@launch

            _isLoading.value = true
            editProfile(country, nickname, username)
        }
    }

    override fun onSelectionChange(item: String) {
        country.value = item
    }

    override fun onNavigateBack() {
        _signUpEvent.value = Event(EditProfileEvent.BackPressed)
    }

    private fun fetchUserInformation() {
        viewModelScope.launch {
            profileRepository.fetchMyUserInformation()
                .onSuccess { userProfile ->
                    setupUserInformation(userProfile)
                }.onFailure {
                    _signUpEvent.value = Event(EditProfileEvent.Error)
                }
        }
    }

    private fun setupUserInformation(userProfile: UserProfile) {
        existingUsername = userProfile.username
        usernameContent.value = userProfile.username
        nicknameContent.value = userProfile.nickname
        country.value = userProfile.region
        imageTitle = userProfile.image
        _imageUri.value = Uri.parse(userProfile.image)
    }

    private fun onEditProfileFailure() {
        _isLoading.value = false
        _signUpEvent.value = Event(EditProfileEvent.Error)
    }

    private fun validation(
        username: String,
        nickname: String,
    ): Boolean {
        if (!validateUsernameUseCase(username)) {
            _signUpEvent.value = Event(EditProfileEvent.UsernameInvalid)
            return false
        }

        if (!validateNicknameUseCase(nickname)) {
            _signUpEvent.value = Event(EditProfileEvent.NicknameLengthInvalid)
            return false
        }

        return true
    }

    private suspend fun editProfile(
        country: String,
        nickname: String,
        username: String,
    ) {
        profileRepository
            .patchMyUserInformation(
                UpdateProfileRequest(username, nickname, imageTitle, country, ""),
            ).onSuccess {
                onEditSuccessful()
            }.onFailure {
                onEditProfileFailure()
            }
    }

    private suspend fun usernameAvailable(username: String): Boolean {
        if (username == existingUsername) return true
        authorizationRepository
            .checkUsernameDuplication(username)
            .onSuccess { available ->
                if (!available) {
                    _signUpEvent.value = Event(EditProfileEvent.NicknameDuplicated)
                    return false
                }
            }.onFailure {
                _signUpEvent.value = Event(EditProfileEvent.Error)
                return false
            }

        return true
    }

    private fun onEditSuccessful() {
        _isLoading.value = false
        _signUpEvent.value = Event(EditProfileEvent.EditProfileSuccessful)
    }
}
