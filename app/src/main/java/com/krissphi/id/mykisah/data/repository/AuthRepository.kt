package com.krissphi.id.mykisah.data.repository

import com.krissphi.id.mykisah.data.local.UserPreference
import com.krissphi.id.mykisah.data.remote.api.ApiService
import com.krissphi.id.mykisah.data.remote.response.LoginResponse
import com.krissphi.id.mykisah.data.remote.response.RegisterResponse

class AuthRepository(
    private var apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        userPreference.deleteTokenKey()
    }
}

