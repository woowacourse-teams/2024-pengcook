package net.pengcook.android.presentation.signup

import android.net.Uri

data class SignUpUiState(
    val isLoading: Boolean = false,
    val profileImage: Uri? = null,
    val fulfilled: Boolean = false,
)
