package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseRegister

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    // Hilangkan parameter phoneNumber
    fun register(email: String, password: String, callback: (ResponseRegister) -> Unit) {
        repository.register(email, password, callback)
    }
}
