package net.pengcook.android.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.domain.model.auth.RenewedTokens
import net.pengcook.android.domain.model.auth.SignInResult

class MainViewModel(
    private val authorizationRepository: AuthorizationRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<MainUiEvent?> = MutableStateFlow(null)
    val uiState: StateFlow<MainUiEvent?> = _uiState.asStateFlow()

    init {
        checkSignInStatus()
    }

    private fun checkSignInStatus() {
        viewModelScope.launch {
            authorizationRepository.checkSignInStatus()
                .onSuccess { signInResult ->
                    handleSignInResult(signInResult)
                }.onFailure {
                    onTokenValidationFailure()
                }
        }
    }

    private suspend fun MainViewModel.handleSignInResult(signInResult: SignInResult) {
        when (signInResult) {
            SignInResult.SUCCESSFUL ->
                _uiState.value = MainUiEvent.NavigateToMain

            SignInResult.ACCESS_TOKEN_EXPIRED -> fetchRenewedTokens()
            else -> onTokenValidationFailure()
        }
    }

    private suspend fun fetchRenewedTokens() {
        authorizationRepository.fetchRenewedTokens()
            .onSuccess { renewedTokens ->
                onTokenRenewalSuccessful(renewedTokens)
            }.onFailure {
                onTokenValidationFailure()
            }
    }

    private suspend fun onTokenRenewalSuccessful(renewedTokens: RenewedTokens) {
        coroutineScope {
            val updateAccessToken =
                launch {
                    sessionRepository.updateAccessToken(renewedTokens.accessToken)
                }
            val updateRefreshToken =
                launch {
                    sessionRepository.updateRefreshToken(renewedTokens.refreshToken)
                }
            joinAll(updateAccessToken, updateRefreshToken)
            _uiState.value = MainUiEvent.NavigateToMain
        }
    }

    private suspend fun onTokenValidationFailure() {
        sessionRepository.clearAll()
        _uiState.value = MainUiEvent.NavigateToOnboarding
    }
}
