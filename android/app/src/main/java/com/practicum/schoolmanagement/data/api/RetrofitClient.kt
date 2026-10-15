package com.practicum.schoolmanagement.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 10.0.2.2 is the localhost alias for the Android Emulator
    private var baseUrl: String = "http://10.0.2.2:5000/"
    
    private var retrofitInstance: Retrofit? = null
    private var apiServiceInstance: ApiService? = null

    fun getBaseUrl(): String = baseUrl

    fun setBaseUrl(url: String) {
        val sanitized = if (url.endsWith("/")) url else "$url/"
        baseUrl = sanitized
        retrofitInstance = null
        apiServiceInstance = null
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun getClient(): ApiService {
        if (apiServiceInstance == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitInstance = retrofit
            apiServiceInstance = retrofit.create(ApiService::class.java)
        }
        return apiServiceInstance!!
    }
}
