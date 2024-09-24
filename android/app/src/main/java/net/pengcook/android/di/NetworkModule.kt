package net.pengcook.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.pengcook.android.BuildConfig
import net.pengcook.android.data.remote.api.AuthorizationService
import net.pengcook.android.data.remote.api.CommentService
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.remote.api.ImageService
import net.pengcook.android.data.remote.api.LikeService
import net.pengcook.android.data.remote.api.MakingRecipeService
import net.pengcook.android.data.remote.api.ProfileService
import net.pengcook.android.data.remote.api.StepMakingService
import net.pengcook.android.data.remote.api.UserControlService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

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
            OkHttpClient.Builder()
                .apply {
                    addInterceptor(interceptor)
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(20, TimeUnit.SECONDS)
                    writeTimeout(25, TimeUnit.SECONDS)
                }.build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(retrofit: Retrofit): AuthorizationService = retrofit.create(AuthorizationService::class.java)

    @Provides
    @Singleton
    fun provideCommentService(retrofit: Retrofit): CommentService = retrofit.create(CommentService::class.java)

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService = retrofit.create(FeedService::class.java)

    @Provides
    @Singleton
    fun provideImageService(retrofit: Retrofit): ImageService = retrofit.create(ImageService::class.java)

    @Provides
    @Singleton
    fun provideLikeService(retrofit: Retrofit): LikeService = retrofit.create(LikeService::class.java)

    @Provides
    @Singleton
    fun provideMakingRecipeService(retrofit: Retrofit): MakingRecipeService = retrofit.create(MakingRecipeService::class.java)

    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService = retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun provideStepMakingService(retrofit: Retrofit): StepMakingService = retrofit.create(StepMakingService::class.java)

    @Provides
    @Singleton
    fun provideUserControlService(retrofit: Retrofit): UserControlService = retrofit.create(UserControlService::class.java)
}
