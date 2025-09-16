package com.krissphi.id.mykisah.ui.page.story.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.krissphi.id.mykisah.data.remote.response.StoryCreateResponse
import com.krissphi.id.mykisah.data.repository.StoryRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class StoryCreateViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<Result<StoryCreateResponse>>()
    val uploadResult: LiveData<Result<StoryCreateResponse>> = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadStory(imageFile: File, description: String, lat: Double?, lon: Double?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.uploadStory(imageFile, description, lat, lon)
                _uploadResult.value = Result.success(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoryCreateResponse::class.java)
                _uploadResult.value = Result.failure(Exception(errorResponse.message))
            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}