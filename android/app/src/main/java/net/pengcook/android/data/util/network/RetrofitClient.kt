package net.pengcook.android.data.util.network

import net.pengcook.android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> service(apiService: Class<T>): T {
        return retrofit.create(apiService)
    }
}
