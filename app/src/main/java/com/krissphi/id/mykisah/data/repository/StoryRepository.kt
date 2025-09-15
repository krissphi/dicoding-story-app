package com.krissphi.id.mykisah.data.repository

import com.krissphi.id.mykisah.data.remote.api.ApiService
import com.krissphi.id.mykisah.data.remote.response.StoryCreateResponse
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository (
    private val apiService : ApiService
) {

    suspend fun getStories(): List<StoryItem> {
        return apiService.getStories().listStory
    }

    suspend fun getStoryDetail(id: String): StoryItem? {
        return apiService.getDetailStory(id).story
    }

    suspend fun uploadStory(imageFile: File, description: String): StoryCreateResponse {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        return apiService.createNewStory(multipartBody, requestBody)
    }

}