package com.example.storyapp.ui.activities.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.utils.DataStatus
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<DataStatus<StoryResponse>>()
    val stories: LiveData<DataStatus<StoryResponse>>
        get() = _stories

    init {
        getStoriesWithLocation()
    }

    private fun getStoriesWithLocation() {
        viewModelScope.launch {
            storyRepository.getStoriesWithLocation().collect {
                _stories.value = it
            }
        }
    }
}