package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseClassification
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    // Untuk hasil upload terbaru
    private val _uploadResult = MutableLiveData<ResponseClassification?>()
    val uploadResult: LiveData<ResponseClassification?> = _uploadResult

    // Untuk daftar semua soal
    private val _questionsList = MutableLiveData<List<ResponseClassification>>(emptyList())
    val questionsList: LiveData<List<ResponseClassification>> = _questionsList

    // Untuk loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Untuk error handling
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _classificationResult = MutableLiveData<ResponseClassification?>()
    val classificationResult: LiveData<ResponseClassification?> get() = _classificationResult

    fun classifyImage(imageFile: File) {
        repository.getResponseClassification(imageFile) { classification ->
            _classificationResult.postValue(classification)
        }
    }

    // User session management
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    // Upload functions
    fun uploadText(text: String) {
        _isLoading.value = true
        repository.uploadText(text) { result ->
            _isLoading.value = false
            result?.let {
                _uploadResult.postValue(it)
                addQuestion(it)
            } ?: run {
                _errorMessage.postValue("Gagal mengupload teks")
            }
        }
    }

    fun uploadImage(imageFile: File) {
        _isLoading.value = true
        repository.uploadImage(imageFile) { result ->
            _isLoading.value = false
            result?.let {
                _uploadResult.postValue(it)
                addQuestion(it)
            } ?: run {
                _errorMessage.postValue("Gagal mengupload gambar")
            }
        }
    }

    // Question list management
    fun addQuestion(question: ResponseClassification) {
        val currentList = _questionsList.value?.toMutableList() ?: mutableListOf()
        currentList.add(0, question) // Tambah di awal list
        _questionsList.postValue(currentList)
    }

    // Reset states
    fun clearUploadResult() {
        _uploadResult.value = null
        _errorMessage.value = null
    }
}