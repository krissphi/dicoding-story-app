package com.krissphi.id.mykisah.ui.page.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import com.krissphi.id.mykisah.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MapsViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<StoryItem>>()
    val storiesWithLocation: LiveData<List<StoryItem>> = _storiesWithLocation

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        fetchStoriesWithLocation()
    }

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val stories = storyRepository.getStoriesWithLocation()
                _storiesWithLocation.value = stories
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

}