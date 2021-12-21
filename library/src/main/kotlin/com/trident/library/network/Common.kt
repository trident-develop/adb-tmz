package com.trident.library.network

object Common {
    private val BASE_URL = "https://www.simplifiedcoding.net/demos/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}