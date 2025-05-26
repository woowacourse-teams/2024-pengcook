package net.pengcook.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.pengcook.android.data.datasource.auth.AuthorizationRemoteDataSource
import net.pengcook.android.data.datasource.auth.DefaultAuthorizationRemoteDataSource
import net.pengcook.android.data.datasource.auth.DefaultSessionLocalDataSource
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.comment.CommentDataSource
import net.pengcook.android.data.datasource.comment.DefaultCommentDataSource
import net.pengcook.android.data.datasource.feed.DefaultFeedRemoteDataSource
import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.datasource.like.DefaultLikeRemoteDataSource
import net.pengcook.android.data.datasource.like.LikeRemoteDataSource
import net.pengcook.android.data.datasource.making.DefaultRecipeStepMakingCacheDataSource
import net.pengcook.android.data.datasource.making.DefaultRecipeStepMakingLocalDataSource
import net.pengcook.android.data.datasource.making.DefaultRecipeStepMakingRemoteDataSource
import net.pengcook.android.data.datasource.making.RecipeStepMakingCacheDataSource
import net.pengcook.android.data.datasource.making.RecipeStepMakingLocalDataSource
import net.pengcook.android.data.datasource.making.RecipeStepMakingRemoteDataSource
import net.pengcook.android.data.datasource.makingrecipe.DefaultMakingRecipeLocalDataSource
import net.pengcook.android.data.datasource.makingrecipe.DefaultMakingRecipeRemoteDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeLocalDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeRemoteDataSource
import net.pengcook.android.data.datasource.photo.DefaultImageRemoteDataSource
import net.pengcook.android.data.datasource.photo.ImageRemoteDataSource
import net.pengcook.android.data.datasource.profile.DefaultProfileRemoteDataSource
import net.pengcook.android.data.datasource.profile.ProfileRemoteDataSource
import net.pengcook.android.data.datasource.usercontrol.DefaultUserControlDataSource
import net.pengcook.android.data.datasource.usercontrol.UserControlDataSource

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {
    @Binds
    fun bindAuthorizationRemoteDataSource(
        defaultAuthorizationRemoteDataSource: DefaultAuthorizationRemoteDataSource,
    ): AuthorizationRemoteDataSource

    @Binds
    fun bindSessionLocalDataSource(defaultSessionLocalDataSource: DefaultSessionLocalDataSource): SessionLocalDataSource

    @Binds
    fun bindFeedRemoteDataSource(defaultFeedRemoteDataSource: DefaultFeedRemoteDataSource): FeedRemoteDataSource

    @Binds
    fun bindMakingRecipeRemoteDataSource(
        defaultMakingRecipeRemoteDataSource: DefaultMakingRecipeRemoteDataSource,
    ): MakingRecipeRemoteDataSource

    @Binds
    fun bindMakingRecipeLocalDataSource(defaultMakingRecipeLocalDataSource: DefaultMakingRecipeLocalDataSource): MakingRecipeLocalDataSource

    @Binds
    fun bindRecipeStepMakingRemoteDataSource(
        defaultRecipeStepMakingRemoteDataSource: DefaultRecipeStepMakingRemoteDataSource,
    ): RecipeStepMakingRemoteDataSource

    @Binds
    fun bindRecipeStepMakingLocalDatasource(
        defaultRecipeStepMakingLocalDataSource: DefaultRecipeStepMakingLocalDataSource,
    ): RecipeStepMakingLocalDataSource

    @Binds
    fun bindRecipeStepMakingCacheDataSource(
        defaultRecipeStepMakingCacheDataSource: DefaultRecipeStepMakingCacheDataSource,
    ): RecipeStepMakingCacheDataSource

    @Binds
    fun bindCommentDataSource(defaultCommentDataSource: DefaultCommentDataSource): CommentDataSource

    @Binds
    fun bindLikeRemoteDataSource(defaultLikeRemoteDataSource: DefaultLikeRemoteDataSource): LikeRemoteDataSource

    @Binds
    fun bindProfileRemoteDataSource(defaultProfileRemoteDataSource: DefaultProfileRemoteDataSource): ProfileRemoteDataSource

    @Binds
    fun bindUserControlRemoteDataSource(defaultUserControlDataSource: DefaultUserControlDataSource): UserControlDataSource

    @Binds
    fun bindImageRemoteDataSource(defaultImageRemoteDataSource: DefaultImageRemoteDataSource): ImageRemoteDataSource
}
