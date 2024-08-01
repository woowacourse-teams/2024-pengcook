package net.pengcook.android.domain.usecase

class ValidateNicknameUseCase {
    operator fun invoke(
        nickname: String,
        lengthRange: IntRange = RANGE_NICKNAME,
    ): Boolean {
        return nickname.length in lengthRange
    }

    companion object {
        private const val MINIMUM_NICKNAME_LENGTH = 1
        private const val MAXIMUM_NICKNAME_LENGTH = 30
        private val RANGE_NICKNAME = MINIMUM_NICKNAME_LENGTH..MAXIMUM_NICKNAME_LENGTH
    }
}
