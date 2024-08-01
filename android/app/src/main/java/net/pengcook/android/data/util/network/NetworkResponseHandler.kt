package net.pengcook.android.data.util.network

import retrofit2.Response

abstract class NetworkResponseHandler {
    fun <T> body(
        response: Response<T>,
        vararg validHttpCode: Int,
    ): T {
        val code = response.code()
        val body = response.body()
        if (code !in validHttpCode) throw RuntimeException(EXCEPTION_HTTP_CODE.format(code))
        if (body == null) throw RuntimeException(EXCEPTION_NULL_BODY)
        return body
    }

    companion object {
        private const val EXCEPTION_HTTP_CODE = "Http code is not appropriate : %d"
        private const val EXCEPTION_NULL_BODY = "Response body is null."
    }
}
