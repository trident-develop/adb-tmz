package com.trident.library.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.Throws

class CustomInterceptor(private val userAgent: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val requestWithUserAgent = originRequest.newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}