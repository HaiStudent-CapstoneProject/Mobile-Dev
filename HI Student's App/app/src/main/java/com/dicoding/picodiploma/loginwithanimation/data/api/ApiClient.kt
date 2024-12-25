package com.dicoding.picodiploma.loginwithanimation.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val AUTH_BASE_URL = "https://capstone-project-442216.et.r.appspot.com/api/auth/"
    private const val OCR_BASE_URL = "https://haistudocr-419393172912.asia-southeast2.run.app/"

    fun provideAuthApiService(): ApiService {
        return createRetrofit(AUTH_BASE_URL).create(ApiService::class.java)
    }

    fun provideOcrApiService(): ApiService {
        return createRetrofit(OCR_BASE_URL).create(ApiService::class.java)
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}