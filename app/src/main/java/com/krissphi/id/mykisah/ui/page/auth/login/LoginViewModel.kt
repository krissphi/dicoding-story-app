package com.krissphi.id.mykisah.ui.page.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krissphi.id.mykisah.data.remote.response.LoginResponse
import com.krissphi.id.mykisah.data.repository.AuthRepository
import com.krissphi.id.mykisah.data.local.UserPreference
import com.krissphi.id.mykisah.data.remote.response.ErrorResponse
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreference: UserPreference
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResponse= MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> = _errorResponse

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _isLoading.value = false

            result.onSuccess { response ->
                response.loginResult?.token?.let { token ->
                    saveToken(token)
                }
                _loginResponse.value = response
            }

            result.onFailure { exception ->
                val errorResponse = when (exception) {
                    is retrofit2.HttpException -> {
                        // Jika error berasal dari server (misal: 400, 401, 500)
                        val errorBody = exception.response()?.errorBody()?.string()
                        try {
                            // Coba ubah JSON error dari server menjadi objek ErrorResponse
                            com.google.gson.Gson().fromJson(errorBody, ErrorResponse::class.java)
                        } catch (e: Exception) {
                            // Jika JSON tidak sesuai atau body kosong
                            ErrorResponse(true, "Terjadi kesalahan server (Kode: ${exception.code()})")
                        }
                    }
                    else -> {
                        ErrorResponse(true, exception.message ?: "Terjadi kesalahan jaringan")
                    }
                }
                _errorResponse.value = errorResponse
            }
        }
    }

    private fun saveToken(token: String) {
        viewModelScope.launch {
            userPreference.saveTokenKey(token)
        }
    }
}