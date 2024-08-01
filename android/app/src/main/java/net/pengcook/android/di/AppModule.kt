package net.pengcook.android.di

import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.TokenRepository
import net.pengcook.android.data.repository.feed.FeedRepository

interface AppModule {
    val authorizationRepository: AuthorizationRepository

    val tokenRepository: TokenRepository

    val feedRepository: FeedRepository

    fun <T> service(apiService: Class<T>): T
}
