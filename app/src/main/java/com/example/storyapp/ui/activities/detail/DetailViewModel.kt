package com.example.storyapp.ui.activities.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.utils.DataStatus
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<DataStatus<DetailStoryResponse>>()
    val story: LiveData<DataStatus<DetailStoryResponse>>
        get() = _story

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            storyRepository.getDetailStory(id).collect {
                _story.value = it
            }
        }
    }

}