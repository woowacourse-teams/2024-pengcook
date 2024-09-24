package net.pengcook.android.domain.usecase

import javax.inject.Inject

class ValidateUsernameUseCase @Inject constructor() {
    operator fun invoke(
        username: String,
        lengthRange: IntRange = RANGE_USERNAME,
    ): Boolean {
        return username.length in lengthRange
    }

    companion object {
        private const val MINIMUM_USERNAME_LENGTH = 2
        private const val MAXIMUM_USERNAME_LENGTH = 30
        private val RANGE_USERNAME = MINIMUM_USERNAME_LENGTH..MAXIMUM_USERNAME_LENGTH
    }
}
