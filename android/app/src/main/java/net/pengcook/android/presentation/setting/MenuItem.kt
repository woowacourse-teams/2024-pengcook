package net.pengcook.android.presentation.setting

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.pengcook.android.R

enum class MenuItem(
    @DrawableRes val drawable: Int,
    @StringRes val title: Int,
) {
    LIKES(R.drawable.ic_search_24, R.string.setting_menu_likes),
    COMMENTS(R.drawable.ic_search_24, R.string.setting_menu_comments),
    MY_COMMENTS(R.drawable.ic_search_24, R.string.setting_menu_my_comments),
    BLOCKED(R.drawable.ic_search_24, R.string.setting_menu_blocked),
    LANGUAGE(R.drawable.ic_search_24, R.string.setting_menu_language),
    PRIVACY_POLICY(R.drawable.ic_search_24, R.string.setting_menu_privacy_policy),
    TERMS_OF_USE(R.drawable.ic_search_24, R.string.setting_menu_privacy_terms),
    ACCOUNT(R.drawable.ic_search_24, R.string.setting_menu_account),
    SIGN_OUT(R.drawable.ic_search_24, R.string.setting_account_sign_out),
    DELETE_ACCOUNT(R.drawable.ic_search_24, R.string.setting_account_delete_account),
}
