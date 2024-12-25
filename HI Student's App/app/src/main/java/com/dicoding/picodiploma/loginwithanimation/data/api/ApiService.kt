package com.dicoding.picodiploma.loginwithanimation.data.api

import com.dicoding.picodiploma.loginwithanimation.data.request.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.data.request.RegisterRequest
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseClassification
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseLogin
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseRegister
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("/api/auth/signin")
    fun login(@Body loginRequest: LoginRequest): Call<ResponseLogin>

    @POST("/api/auth/signup")
    fun register(@Body registerRequest: RegisterRequest): Call<ResponseRegister>

    @POST("/process")
    @Multipart
    fun getClassification(
        @Part image: MultipartBody.Part
    ): Call<ResponseClassification>

    @POST("/process")
    @FormUrlEncoded
    fun getTextClassification(
        @Field("text") text: String
    ): Call<ResponseClassification>
}

