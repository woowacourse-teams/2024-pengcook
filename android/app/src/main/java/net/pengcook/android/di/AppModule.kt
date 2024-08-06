package net.pengcook.android.di

import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository

interface AppModule {
    val authorizationRepository: AuthorizationRepository

    val sessionRepository: SessionRepository

    val feedRepository: FeedRepository

    val recipeStepMakingRepository: RecipeStepMakingRepository

    fun <T> service(apiService: Class<T>): T
}
