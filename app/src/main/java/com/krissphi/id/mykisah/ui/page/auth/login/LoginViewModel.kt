package com.krissphi.id.mykisah.ui.page.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krissphi.id.mykisah.data.remote.response.LoginResponse
import com.krissphi.id.mykisah.data.repository.AuthRepository
import com.krissphi.id.mykisah.data.local.UserPreference
import com.krissphi.id.mykisah.data.remote.response.ErrorResponse
import com.krissphi.id.mykisah.utils.EspressoIdlingResource
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreference: UserPreference
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _errorResponse = MutableLiveData<ErrorResponse>()
    val errorResponse: LiveData<ErrorResponse> = _errorResponse

    fun login(email: String, password: String) {
        EspressoIdlingResource.increment()
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _isLoading.value = false

            result.onSuccess { response ->
                response.loginResult?.token?.let { token ->
                    saveToken(token)
                }
                _loginResponse.value = response
                EspressoIdlingResource.decrement()
            }

            result.onFailure { exception ->
                val errorResponse = when (exception) {
                    is retrofit2.HttpException -> {
                        val errorBody = exception.response()?.errorBody()?.string()
                        try {
                            com.google.gson.Gson().fromJson(errorBody, ErrorResponse::class.java)
                        } catch (_: Exception) {
                            ErrorResponse(
                                true,
                                "Terjadi kesalahan server (Kode: ${exception.code()})"
                            )
                        }
                    }

                    else -> {
                        ErrorResponse(true, exception.message ?: "Terjadi kesalahan jaringan")
                    }
                }
                _errorResponse.value = errorResponse
                EspressoIdlingResource.decrement()
            }
        }
    }

    private fun saveToken(token: String) {
        viewModelScope.launch {
            userPreference.saveTokenKey(token)
        }
    }
}