package com.krissphi.id.mykisah.ui.page.story.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import com.krissphi.id.mykisah.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryDetailViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _story = MutableLiveData<StoryItem>()
    val story: LiveData<StoryItem> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchStoryDetail(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val storyDetail = storyRepository.getStoryDetail(id)
                if (storyDetail != null) {
                    _story.value = storyDetail
                } else {
                    _errorMessage.value = "STORY_NOT_FOUND"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}