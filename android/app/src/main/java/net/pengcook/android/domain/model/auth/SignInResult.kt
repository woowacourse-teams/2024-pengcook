package net.pengcook.android.domain.model.auth

enum class SignInResult {
    SUCCESSFUL,
    ACCESS_TOKEN_EXPIRED,
    USER_NOT_FOUND,
    SERVER_ERROR,
}
