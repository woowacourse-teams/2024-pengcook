package net.pengcook.android.presentation.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.core.util.Event

class SignUpViewModel :
    ViewModel(),
    BottomButtonClickListener {
    var usernameContent: MutableLiveData<String> = MutableLiveData("")
    var nicknameContent: MutableLiveData<String> = MutableLiveData("")
    var year: MutableLiveData<String> = MutableLiveData("")
    var month: MutableLiveData<String> = MutableLiveData("")
    var day: MutableLiveData<String> = MutableLiveData("")
    var country: MutableLiveData<String> = MutableLiveData("")
    private var _imageUri: MutableLiveData<Uri> = MutableLiveData()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    private var _signUpUiState: MutableLiveData<SignUpUiState> = MutableLiveData(SignUpUiState())
    val signUpUiState: LiveData<SignUpUiState>
        get() = _signUpUiState

    private var _signUpEvent: MutableLiveData<Event<SignUpEvent>> = MutableLiveData()
    val signUpEvent: LiveData<Event<SignUpEvent>>
        get() = _signUpEvent

    fun changeProfileImage(uri: Uri) {
        _imageUri.value = uri
    }

    override fun onConfirm() {
        _signUpUiState.value = signUpUiState.value?.copy(isLoading = true)
        _signUpEvent.value = Event(SignUpEvent.NavigateToMain("", ""))
        _signUpUiState.value = signUpUiState.value?.copy(isLoading = false)
    }
}