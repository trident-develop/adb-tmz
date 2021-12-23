package com.trident.library.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {


         // Get android user agent.
        var okHttp: OkHttpClient = OkHttpClient()
        okHttp.interceptors().add(CustomInterceptor(System.getProperty("http.agent")))
        //OkHttpClient okHttp = new OkHttpClient()
        //okHttp.interceptors().add(new UserAgentInterceptor(UA));

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp)
                .build()
        }
        return retrofit!!
    }
}