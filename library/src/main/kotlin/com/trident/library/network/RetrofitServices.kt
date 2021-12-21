package com.trident.library.network

import com.trident.library.constants.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface RetrofitServices {
    @GET
    fun checkStatus(@Url url: String): Call<ResponseBody>
}