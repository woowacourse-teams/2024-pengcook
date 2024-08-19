package net.pengcook.android.presentation.setting.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository

class AccountSettingViewModelFactory(
    private val sessionRepository: SessionRepository,
    private val authorizationRepository: AuthorizationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountSettingViewModel::class.java)) {
            return AccountSettingViewModel(sessionRepository, authorizationRepository) as T
        }
        throw IllegalArgumentException()
    }
}
