package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.request.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.data.request.RegisterRequest
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseClassification
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseLogin
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseRegister
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val authApiService: ApiService,
    private val ocrApiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun uploadImage(imageFile: File, callback: (ResponseClassification?) -> Unit) {
        try {
            val requestImageFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartImage = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )

            val call = ocrApiService.getClassification(multipartImage)
            call.enqueue(object : Callback<ResponseClassification> {
                override fun onResponse(
                    call: Call<ResponseClassification>,
                    response: Response<ResponseClassification>
                ) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    } else {
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ResponseClassification>, t: Throwable) {
                    Log.e("Upload", "Error: ${t.message}")
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e("Upload", "Error: ${e.message}")
            callback(null)
        }
    }

    fun uploadText(text: String, callback: (ResponseClassification) -> Unit) {
        val call = ocrApiService.getTextClassification(text)
        call.enqueue(object : Callback<ResponseClassification> {
            override fun onResponse(
                call: Call<ResponseClassification>,
                response: Response<ResponseClassification>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(it) }
                }
            }

            override fun onFailure(call: Call<ResponseClassification>, t: Throwable) {
                Log.e("Upload", "Error: ${t.message}")
            }
        })
    }


    // Tambahkan fungsi baru untuk login
    fun login(
        email: String,
        password: String,
        callback: (ResponseLogin) -> Unit
    ) {
        val request = LoginRequest(email, password)
        val call = authApiService.login(request)
        call.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(it) }
                } else {
                    callback(ResponseLogin(success = false, message = response.message()))
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                callback(ResponseLogin(success = false, message = t.message))
            }
        })
    }


    // Tambahkan fungsi baru untuk register
    fun register(
        email: String,
        password: String,
        callback: (ResponseRegister) -> Unit
    ) {
        val call = authApiService.register(RegisterRequest(email, password))
        call.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(it) }
                } else {
                    Log.e("Register", "Failed: ${response.message()} - ${response.code()}")
                    callback(ResponseRegister(success = false, message = response.message()))
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                Log.e("Register", "Error: ${t.message}")
                callback(ResponseRegister(success = false, message = t.message))
            }
        })
    }

    fun getResponseClassification(imageFile: File, callback: (ResponseClassification?) -> Unit) {
        uploadImage(imageFile) { classification ->
            // Memastikan forumLink tidak null
            val validClassification = classification?.apply {
                if (forumLink.isNullOrEmpty()) {
                    // Jika forumLink kosong atau null, kita set nilai default
                    forumLink = "No Forum Link Available"
                }
            }
            callback(validClassification)
        }
    }



    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            authApiService: ApiService,
            ocrApiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, authApiService, ocrApiService)
            }.also { instance = it }
    }
}
