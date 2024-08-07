package net.pengcook.android.di

import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.data.repository.profile.ProfileRepository

interface AppModule {
    val authorizationRepository: AuthorizationRepository

    val sessionRepository: SessionRepository

    val feedRepository: FeedRepository

    val recipeStepMakingRepository: RecipeStepMakingRepository

    val profileRepository: ProfileRepository

    fun <T> service(apiService: Class<T>): T
}
