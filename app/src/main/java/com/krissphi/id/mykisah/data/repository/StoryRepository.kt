package com.krissphi.id.mykisah.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.krissphi.id.mykisah.data.local.StoryDatabase
import com.krissphi.id.mykisah.data.local.StoryRemoteMediator
import com.krissphi.id.mykisah.data.remote.api.ApiService
import com.krissphi.id.mykisah.data.remote.response.StoryCreateResponse
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {

    fun getStoriesPaging(): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }


    suspend fun getStoryDetail(id: String): StoryItem? {
        return apiService.getDetailStory(id).story
    }

    suspend fun uploadStory(
        imageFile: File,
        description: String,
        lat: Double?,
        lon: Double?
    ): StoryCreateResponse {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

        return apiService.createNewStory(
            multipartBody,
            requestBody,
            latRequestBody,
            lonRequestBody
        )
    }

    suspend fun getStoriesWithLocation(): List<StoryItem> {
        return apiService.getStoriesWithLocation().listStory
    }

}