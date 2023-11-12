package com.example.storyapp.ui.activities.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.BasicResponse
import com.example.storyapp.utils.DataStatus
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _response = MutableLiveData<DataStatus<BasicResponse>>()
    val response: LiveData<DataStatus<BasicResponse>>
        get() = _response

    private val _loginResponse = MutableLiveData<DataStatus<LoginResponse>>()
    val loginResponse: LiveData<DataStatus<LoginResponse>>
        get() = _loginResponse

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            userRepository.register(name, email, password).collect {
                _response.value = it
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.login(email, password).collect {
                _loginResponse.value = it
            }
        }
    }


    suspend fun isLoggedIn(): Boolean {
        return userRepository.getAuthToken()
            .map { token -> token.isNotEmpty() }
            .first()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}