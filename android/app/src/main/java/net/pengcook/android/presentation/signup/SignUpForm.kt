package net.pengcook.android.presentation.signup

import java.io.File

data class SignUpForm(
    val profileImage: File? = null,
    val username: String = INITIAL_VALUE,
    val nickname: String = INITIAL_VALUE,
    val yearOfBirthday: Int = INITIAL_YEAR,
    val monthOfBirthday: Int = INITIAL_MONTH,
    val dayOfBirthday: Int = INITIAL_DAY,
    val country: String = INITIAL_COUNTRY,
) {
    companion object {
        private const val INITIAL_VALUE = ""
        private const val INITIAL_YEAR = 2024
        private const val INITIAL_MONTH = 1
        private const val INITIAL_DAY = 1
        private const val INITIAL_COUNTRY = ""
    }
}
