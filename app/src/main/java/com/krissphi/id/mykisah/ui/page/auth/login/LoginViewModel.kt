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

    private val _errorMessage = MutableLiveData<ErrorResponse>()
    val errorMessage: LiveData<ErrorResponse> = _errorMessage

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

            result.onFailure {
                val errorResponse = if (it is retrofit2.HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        try {
                            val gson = com.google.gson.Gson()
                            gson.fromJson(errorBody, ErrorResponse::class.java)
                        } catch (e: Exception) {
                            ErrorResponse(error = true, message = "An unknown error occurred")
                        }
                    } else {
                        ErrorResponse(error = true, message = "An unknown error occurred")
                    }
                } else {
                    ErrorResponse(error = true, message = it.message ?: "An unknown error occurred")
                }
                _errorMessage.value = errorResponse
            }
        }
    }

    private fun saveToken(token: String) {
        viewModelScope.launch {
            userPreference.saveTokenKey(token)
        }
    }
}