package net.pengcook.android.di

import android.content.Context
import net.pengcook.android.BuildConfig
import net.pengcook.android.data.datasource.auth.AuthorizationRemoteDataSource
import net.pengcook.android.data.datasource.auth.DefaultAuthorizationRemoteDataSource
import net.pengcook.android.data.datasource.auth.DefaultSessionLocalDataSource
import net.pengcook.android.data.datasource.auth.SessionLocalDataSource
import net.pengcook.android.data.datasource.comment.CommentDataSource
import net.pengcook.android.data.datasource.comment.DefaultCommentDataSource
import net.pengcook.android.data.datasource.feed.DefaultFeedRemoteDataSource
import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.datasource.like.DefaultLikeRemoteDataSource
import net.pengcook.android.data.datasource.making.DefaultRecipeStepMakingDataSource
import net.pengcook.android.data.datasource.making.RecipeStepMakingDataSource
import net.pengcook.android.data.datasource.makingrecipe.DefaultMakingRecipeRemoteDataSource
import net.pengcook.android.data.datasource.makingrecipe.MakingRecipeRemoteDataSource
import net.pengcook.android.data.datasource.profile.DefaultProfileRemoteDataSource
import net.pengcook.android.data.datasource.profile.ProfileRemoteDataSource
import net.pengcook.android.data.local.preferences.dataStore
import net.pengcook.android.data.remote.api.AuthorizationService
import net.pengcook.android.data.remote.api.CommentService
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.remote.api.LikeService
import net.pengcook.android.data.remote.api.MakingRecipeService
import net.pengcook.android.data.remote.api.ProfileService
import net.pengcook.android.data.remote.api.StepMakingService
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
import net.pengcook.android.data.repository.profile.DefaultProfileRepository
import net.pengcook.android.data.repository.profile.ProfileRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DefaultAppModule(
    appContext: Context,
) : AppModule {
    private val interceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val client =
        OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(20, TimeUnit.SECONDS)
            writeTimeout(25, TimeUnit.SECONDS)
        }.build()

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    override fun <T> service(apiService: Class<T>): T = retrofit.create(apiService)

    private val authorizationRemoteDataSource: AuthorizationRemoteDataSource =
        DefaultAuthorizationRemoteDataSource(service(AuthorizationService::class.java))

    override val authorizationRepository: AuthorizationRepository =
        DefaultAuthorizationRepository(authorizationRemoteDataSource)

    private val sessionLocalDataSource: SessionLocalDataSource =
        DefaultSessionLocalDataSource(appContext.dataStore)

    override val sessionRepository: SessionRepository =
        DefaultSessionRepository(sessionLocalDataSource)

    private val feedRemoteDataSource: FeedRemoteDataSource =
        DefaultFeedRemoteDataSource(service(FeedService::class.java))

    override val feedRepository: FeedRepository =
        DefaultFeedRepository(feedRemoteDataSource)

    private val makingRecipeRemoteDataSource: MakingRecipeRemoteDataSource =
        DefaultMakingRecipeRemoteDataSource(service(MakingRecipeService::class.java))

    override val makingRecipeRepository: MakingRecipeRepository =
        DefaultMakingRecipeRepository(sessionLocalDataSource, makingRecipeRemoteDataSource)

    private val recipeStepMakingDatasource: RecipeStepMakingDataSource =
        DefaultRecipeStepMakingDataSource(service(StepMakingService::class.java))

    override val recipeStepMakingRepository: RecipeStepMakingRepository =
        DefaultRecipeStepMakingRepository(recipeStepMakingDatasource)

    private val commentDataSource: CommentDataSource =
        DefaultCommentDataSource(service(CommentService::class.java))

    override val commentRepository: CommentRepository =
        DefaultCommentRepository(sessionLocalDataSource, commentDataSource)

    private val likeRemoteDataSource = DefaultLikeRemoteDataSource(service(LikeService::class.java))

    override val likeRepository: LikeRepository =
        DefaultLikeRepository(sessionLocalDataSource, likeRemoteDataSource)

    private val profileRemoteDataSource: ProfileRemoteDataSource =
        DefaultProfileRemoteDataSource(
            service(ProfileService::class.java),
            service(FeedService::class.java),
        )

    override val profileRepository: ProfileRepository =
        DefaultProfileRepository(sessionLocalDataSource, profileRemoteDataSource)
}
