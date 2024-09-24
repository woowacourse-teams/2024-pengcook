package net.pengcook.android.presentation.setting.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.setting.MenuItem
import net.pengcook.android.presentation.setting.SettingMenuItemClickListener
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
        private val authorizationRepository: AuthorizationRepository,
    ) : ViewModel(),
        SettingMenuItemClickListener,
        AppbarSingleActionEventListener {
        private val _uiEvent: MutableLiveData<Event<AccountSettingUiEvent>> = MutableLiveData()
        val uiEvent: LiveData<Event<AccountSettingUiEvent>>
            get() = _uiEvent

        override fun onSettingMenuItemClick(menuItem: MenuItem) {
            when (menuItem) {
                MenuItem.SIGN_OUT -> signOut()
                MenuItem.DELETE_ACCOUNT -> deleteAccount()
                else -> Unit
            }
        }

        private fun deleteAccount() {
            viewModelScope.launch {
                authorizationRepository.deleteAccount()
                sessionRepository.clearAll()
                _uiEvent.value = Event(AccountSettingUiEvent.DeleteAccount)
            }
        }

        private fun signOut() {
            viewModelScope.launch {
                sessionRepository.clearAll()
                _uiEvent.value = Event(AccountSettingUiEvent.SignOut)
            }
        }

        override fun onNavigateBack() {
            _uiEvent.value = Event(AccountSettingUiEvent.NavigateBack)
        }
    }
