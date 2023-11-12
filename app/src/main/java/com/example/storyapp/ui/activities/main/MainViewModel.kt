package com.example.storyapp.ui.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.entity.Story
import kotlinx.coroutines.flow.Flow


class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() : Flow<PagingData<Story>> {
        return storyRepository.getStories().cachedIn(viewModelScope)
    }
}