package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.local.datastore.UserPreference
import com.example.storyapp.data.local.datastore.datastore
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context) : UserRepository {
        val preference = UserPreference.getInstance(context.datastore)
        val token = runBlocking { preference.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        return UserRepository.getInstance(apiService, preference)
    }

    fun provideStoryRepository(context: Context) : StoryRepository {
        val preference = UserPreference.getInstance(context.datastore)
        val token = runBlocking { preference.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(storyDatabase , apiService)
    }
}