package net.pengcook.android.data.util.network

import retrofit2.Response

interface NetworkResponseHandler {
    fun <T> body(
        response: Response<T>,
        validHttpCode: Int,
    ): T
}
