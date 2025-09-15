package com.krissphi.id.mykisah.data.repository

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krissphi.id.mykisah.ui.page.auth.login.LoginViewModel
import com.krissphi.id.mykisah.ui.page.auth.register.RegisterViewModel
import com.krissphi.id.mykisah.ui.page.story.create.StoryCreateViewModel
import com.krissphi.id.mykisah.ui.page.story.detail.StoryDetailViewModel
import com.krissphi.id.mykisah.ui.page.story.main.StoryViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.provideAuthRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserPreference(context)
                ) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(
                    Injection.provideStoryRepository(context),
                    Injection.provideAuthRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(Injection.provideStoryRepository(context)) as T
            }
            modelClass.isAssignableFrom(StoryCreateViewModel::class.java) -> {
                StoryCreateViewModel(Injection.provideStoryRepository(context)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}