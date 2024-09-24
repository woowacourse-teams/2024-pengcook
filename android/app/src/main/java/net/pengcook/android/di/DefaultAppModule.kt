package net.pengcook.android.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
import net.pengcook.android.data.local.database.RecipeDatabase
import net.pengcook.android.data.local.database.dao.CategoryDao
import net.pengcook.android.data.local.database.dao.IngredientDao
import net.pengcook.android.data.local.database.dao.RecipeDescriptionDao
import net.pengcook.android.data.local.database.dao.RecipeStepDao
import net.pengcook.android.data.local.preferences.dataStore
import net.pengcook.android.data.remote.api.AuthorizationService
import net.pengcook.android.data.remote.api.CommentService
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.remote.api.ImageService
import net.pengcook.android.data.remote.api.LikeService
import net.pengcook.android.data.remote.api.MakingRecipeService
import net.pengcook.android.data.remote.api.ProfileService
import net.pengcook.android.data.remote.api.StepMakingService
import net.pengcook.android.data.remote.api.UserControlService
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

class DefaultAppModule(
    appContext: Context,
) : AppModule {
    private val interceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val client =
        OkHttpClient
            .Builder()
            .apply {
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

    private val database = RecipeDatabase.getInstance(appContext)

    override fun <T> service(apiService: Class<T>): T = retrofit.create(apiService)

    private val authorizationRemoteDataSource: AuthorizationRemoteDataSource =
        DefaultAuthorizationRemoteDataSource(service(AuthorizationService::class.java))

    private val sessionLocalDataSource: SessionLocalDataSource =
        DefaultSessionLocalDataSource(appContext.dataStore)

    override val sessionRepository: SessionRepository =
        DefaultSessionRepository(sessionLocalDataSource)

    override val authorizationRepository: AuthorizationRepository =
        DefaultAuthorizationRepository(authorizationRemoteDataSource, sessionLocalDataSource)

    private val feedRemoteDataSource: FeedRemoteDataSource =
        DefaultFeedRemoteDataSource(service(FeedService::class.java))

    override val feedRepository: FeedRepository =
        DefaultFeedRepository(
            sessionLocalDataSource = sessionLocalDataSource,
            feedRemoteDataSource = feedRemoteDataSource,
        )

    private val makingRecipeRemoteDataSource: MakingRecipeRemoteDataSource =
        DefaultMakingRecipeRemoteDataSource(
            service(MakingRecipeService::class.java),
        )

    private val makingRecipeLocalDataSource: MakingRecipeLocalDataSource =
        DefaultMakingRecipeLocalDataSource(database)

    override val makingRecipeRepository: MakingRecipeRepository =
        DefaultMakingRecipeRepository(
            sessionLocalDataSource = sessionLocalDataSource,
            makingRecipeRemoteDataSource = makingRecipeRemoteDataSource,
            makingRecipeLocalDataSource = makingRecipeLocalDataSource,
        )

    private val recipeStepMakingLocalDatasource: RecipeStepMakingLocalDataSource =
        DefaultRecipeStepMakingLocalDataSource(database.recipeStepDao())

    private val recipeStepMakingCacheDataSource: RecipeStepMakingCacheDataSource =
        DefaultRecipeStepMakingCacheDataSource()

    override val recipeStepMakingRepository: RecipeStepMakingRepository =
        DefaultRecipeStepMakingRepository(
            recipeStepMakingLocalDataSource = recipeStepMakingLocalDatasource,
            recipeStepMakingCacheDataSource = recipeStepMakingCacheDataSource,
        )

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

    private val userControlRemoteDataSource: UserControlDataSource =
        DefaultUserControlDataSource(service(UserControlService::class.java))

    override val userControlRepository: UserControlRepository =
        DefaultUserControlRepository(sessionLocalDataSource, userControlRemoteDataSource)

    private val imageRemoteDataSource: ImageRemoteDataSource =
        DefaultImageRemoteDataSource(service(ImageService::class.java))

    override val imageRepository: ImageRepository =
        DefaultImageRepository(imageRemoteDataSource)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideRecipeDatabase(
        @ApplicationContext context: Context,
    ): RecipeDatabase = RecipeDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideRecipeStepDao(recipeDatabase: RecipeDatabase): RecipeStepDao =
        recipeDatabase.recipeStepDao()

    @Provides
    @Singleton
    fun provideRecipeDescriptionDao(recipeDatabase: RecipeDatabase): RecipeDescriptionDao =
        recipeDatabase.recipeDescriptionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(recipeDatabase: RecipeDatabase): CategoryDao =
        recipeDatabase.categoryDao()

    @Provides
    @Singleton
    fun provideIngredientDao(recipeDatabase: RecipeDatabase): IngredientDao =
        recipeDatabase.ingredientDao()
}

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofitClient(): Retrofit {
        val interceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        val client =
            OkHttpClient
                .Builder()
                .apply {
                    addInterceptor(interceptor)
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(20, TimeUnit.SECONDS)
                    writeTimeout(25, TimeUnit.SECONDS)
                }.build()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(retrofit: Retrofit): AuthorizationService =
        retrofit.create(AuthorizationService::class.java)

    @Provides
    @Singleton
    fun provideCommentService(retrofit: Retrofit): CommentService =
        retrofit.create(CommentService::class.java)

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService =
        retrofit.create(FeedService::class.java)

    @Provides
    @Singleton
    fun provideImageService(retrofit: Retrofit): ImageService =
        retrofit.create(ImageService::class.java)

    @Provides
    @Singleton
    fun provideLikeService(retrofit: Retrofit): LikeService =
        retrofit.create(LikeService::class.java)

    @Provides
    @Singleton
    fun provideMakingRecipeService(retrofit: Retrofit): MakingRecipeService =
        retrofit.create(MakingRecipeService::class.java)

    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun provideStepMakingService(retrofit: Retrofit): StepMakingService =
        retrofit.create(StepMakingService::class.java)

    @Provides
    @Singleton
    fun provideUserControlService(retrofit: Retrofit): UserControlService =
        retrofit.create(UserControlService::class.java)
}

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

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = context.dataStore
}
