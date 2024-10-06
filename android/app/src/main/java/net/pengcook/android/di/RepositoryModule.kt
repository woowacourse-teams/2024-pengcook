package net.pengcook.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.DefaultAuthorizationRepository
import net.pengcook.android.data.repository.auth.DefaultSessionRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.data.repository.comment.CommentRepository
import net.pengcook.android.data.repository.comment.DefaultCommentRepository
import net.pengcook.android.data.repository.feed.DefaultFeedRepository
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.data.repository.like.DefaultLikeRepository
import net.pengcook.android.data.repository.like.LikeRepository
import net.pengcook.android.data.repository.making.step.DefaultRecipeStepMakingRepository
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import net.pengcook.android.data.repository.makingrecipe.DefaultMakingRecipeRepository
import net.pengcook.android.data.repository.makingrecipe.MakingRecipeRepository
import net.pengcook.android.data.repository.photo.DefaultImageRepository
import net.pengcook.android.data.repository.photo.ImageRepository
import net.pengcook.android.data.repository.profile.DefaultProfileRepository
import net.pengcook.android.data.repository.profile.ProfileRepository
import net.pengcook.android.data.repository.usercontrol.DefaultUserControlRepository
import net.pengcook.android.data.repository.usercontrol.UserControlRepository

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindSessionRepository(defaultSessionRepository: DefaultSessionRepository): SessionRepository

    @Binds
    fun bindAuthorizationRepository(defaultAuthorizationRepository: DefaultAuthorizationRepository): AuthorizationRepository

    @Binds
    fun bindFeedRepository(defaultFeedRepository: DefaultFeedRepository): FeedRepository

    @Binds
    fun bindMakingRecipeRepository(defaultMakingRecipeRepository: DefaultMakingRecipeRepository): MakingRecipeRepository

    @Binds
    fun bindRecipeStepMakingRepository(defaultRecipeStepMakingRepository: DefaultRecipeStepMakingRepository): RecipeStepMakingRepository

    @Binds
    fun bindCommentRepository(defaultCommentRepository: DefaultCommentRepository): CommentRepository

    @Binds
    fun bindLikeRepository(defaultLikeRepository: DefaultLikeRepository): LikeRepository

    @Binds
    fun bindProfileRepository(defaultProfileRepository: DefaultProfileRepository): ProfileRepository

    @Binds
    fun bindUserControlRepository(defaultUserControlRepository: DefaultUserControlRepository): UserControlRepository

    @Binds
    fun bindImageRepository(defaultImageRepository: DefaultImageRepository): ImageRepository
}
