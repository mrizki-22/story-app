package com.example.storyapp.ui.activities.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.BasicResponse
import com.example.storyapp.utils.DataStatus
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel (private val storyRepository: StoryRepository) : ViewModel() {
    private val _response = MutableLiveData<DataStatus<BasicResponse>>()
    val response: LiveData<DataStatus<BasicResponse>>
        get() = _response

    fun uploadStory(file: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?) {
        viewModelScope.launch {
            storyRepository.uploadStory(file, description, lat, lon).collect {
                _response.value = it
            }
        }
    }
}