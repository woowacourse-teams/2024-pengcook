package net.pengcook.android.presentation.signup

import java.io.File

interface SignUpFormEventListener {
    fun onProfileImageChange(image: File)

    fun onUsernameChange(username: String)

    fun onNicknameChange(nickname: String)

    fun onYearOfBirthChange(year: Int)

    fun onMonthOfBirthChange(month: Int)

    fun onDayOfBirthChange(day: Int)

    fun onCountryChange(country: String)
}
