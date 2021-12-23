package com.trident.library.network

import com.trident.library.constants.Constants

object Common {
    private val BASE_URL = "https://www.simplifiedcoding.net/demos/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL, Constants.appsContext).create(RetrofitServices::class.java)
}