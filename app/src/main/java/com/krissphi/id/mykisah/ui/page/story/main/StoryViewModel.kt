package com.krissphi.id.mykisah.ui.page.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import com.krissphi.id.mykisah.data.repository.AuthRepository
import com.krissphi.id.mykisah.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel(
    storyRepository: StoryRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val stories: LiveData<PagingData<StoryItem>> =
        storyRepository.getStoriesPaging().cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

}