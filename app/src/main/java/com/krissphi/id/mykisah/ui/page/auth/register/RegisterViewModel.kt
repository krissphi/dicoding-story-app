package com.krissphi.id.mykisah.ui.page.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krissphi.id.mykisah.data.remote.response.ErrorResponse
import com.krissphi.id.mykisah.data.remote.response.RegisterResponse
import com.krissphi.id.mykisah.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registrationResponse = MutableLiveData<RegisterResponse>()
    val registrationResponse: LiveData<RegisterResponse> = _registrationResponse

    private val _errorMessage = MutableLiveData<ErrorResponse>()
    val errorMessage: LiveData<ErrorResponse> = _errorMessage

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.register(name, email, password)
            _isLoading.value = false

            result.onSuccess { response ->
                _registrationResponse.value = response
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

}