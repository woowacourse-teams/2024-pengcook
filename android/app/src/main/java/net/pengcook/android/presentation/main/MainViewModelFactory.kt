package net.pengcook.android.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository

class MainViewModelFactory(
    private val authorizationRepository: AuthorizationRepository,
    private val sessionRepository: SessionRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(authorizationRepository, sessionRepository) as T
        }
        throw IllegalArgumentException()
    }
}
