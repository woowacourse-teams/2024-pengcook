package net.pengcook.android.di

import android.content.Context
import net.pengcook.android.BuildConfig
import net.pengcook.android.data.datasource.auth.AuthorizationRemoteDataSource
import net.pengcook.android.data.datasource.auth.DefaultAuthorizationRemoteDataSource
import net.pengcook.android.data.datasource.auth.DefaultTokenLocalDataSource
import net.pengcook.android.data.datasource.auth.TokenLocalDataSource
import net.pengcook.android.data.datasource.feed.DefaultFeedRemoteDataSource
import net.pengcook.android.data.datasource.feed.FeedRemoteDataSource
import net.pengcook.android.data.local.preferences.dataStore
import net.pengcook.android.data.remote.api.AuthorizationService
import net.pengcook.android.data.remote.api.FeedService
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.DefaultAuthorizationRepository
import net.pengcook.android.data.repository.auth.DefaultTokenRepository
import net.pengcook.android.data.repository.auth.TokenRepository
import net.pengcook.android.data.repository.feed.DefaultFeedRepository
import net.pengcook.android.data.repository.feed.FeedRepository
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
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    override fun <T> service(apiService: Class<T>): T {
        return retrofit.create(apiService)
    }

    private val authorizationRemoteDataSource: AuthorizationRemoteDataSource =
        DefaultAuthorizationRemoteDataSource(service(AuthorizationService::class.java))

    override val authorizationRepository: AuthorizationRepository =
        DefaultAuthorizationRepository(authorizationRemoteDataSource)

    private val tokenLocalDataSource: TokenLocalDataSource =
        DefaultTokenLocalDataSource(appContext.dataStore)

    override val tokenRepository: TokenRepository =
        DefaultTokenRepository(tokenLocalDataSource)

    private val feedRemoteDataSource: FeedRemoteDataSource =
        DefaultFeedRemoteDataSource(service(FeedService::class.java))

    override val feedRepository: FeedRepository =
        DefaultFeedRepository(feedRemoteDataSource)
}
