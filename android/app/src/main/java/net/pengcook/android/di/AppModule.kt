package net.pengcook.android.di

import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.data.repository.feed.FeedRepository

interface AppModule {
    val authorizationRepository: AuthorizationRepository

    val sessionRepository: SessionRepository

    val feedRepository: FeedRepository

    fun <T> service(apiService: Class<T>): T
}