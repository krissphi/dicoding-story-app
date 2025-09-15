package com.krissphi.id.mykisah.data.repository

import android.content.Context
import com.krissphi.id.mykisah.data.local.UserPreference
import com.krissphi.id.mykisah.data.local.dataStore
import com.krissphi.id.mykisah.data.remote.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService("")
        val userPreference = provideUserPreference(context)
        return AuthRepository(apiService, userPreference)
    }
    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getTokenKey().first() }
        val apiService = ApiConfig.getApiService(user)
        return StoryRepository(apiService)
    }
}