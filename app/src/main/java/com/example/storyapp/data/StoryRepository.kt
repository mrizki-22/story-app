package com.example.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.local.entity.Story
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.DataStatus
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStories() : Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
//            pagingSourceFactory = { StoryPagingSource(apiService) }
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }

    suspend fun getDetailStory(id: String) = flow {
        emit(DataStatus.loading())
        val result = apiService.getDetailStory(id)
        when(result.code()){
            200 -> emit(DataStatus.success(result.body()))
            else -> {
                val errorBody = result.errorBody()?.string()
                val jsonParser = JsonParser()
                val jsonObject = jsonParser.parse(errorBody).asJsonObject
                val message = jsonObject.get("message").asString
                emit(DataStatus.error(message))
            }
        }
    } .catch {
        emit(DataStatus.error(it.message.toString()))
    } .flowOn(Dispatchers.IO)

    suspend fun uploadStory(file : MultipartBody.Part, description : RequestBody, lat : RequestBody?, lon : RequestBody?) = flow {
        emit(DataStatus.loading())
        val result = apiService.uploadStory(file, description, lat, lon)
        when(result.code()){
            201 -> emit(DataStatus.success(result.body()))
            else -> {
                val errorBody = result.errorBody()?.string()
                val jsonParser = JsonParser()
                val jsonObject = jsonParser.parse(errorBody).asJsonObject
                val message = jsonObject.get("message").asString
                emit(DataStatus.error(message))
            }
        }
    } .catch {
        emit(DataStatus.error(it.message.toString()))
    } .flowOn(Dispatchers.IO)

    suspend fun getStoriesWithLocation() = flow {
        emit(DataStatus.loading())
        val result = apiService.getStories(null, null, 1)
        when(result.code()){
            200 -> emit(DataStatus.success(result.body()))
            else -> {
                val errorBody = result.errorBody()?.string()
                val jsonParser = JsonParser()
                val jsonObject = jsonParser.parse(errorBody).asJsonObject
                val message = jsonObject.get("message").asString
                emit(DataStatus.error(message))
            }
        }
    } .catch {
        emit(DataStatus.error(it.message.toString()))
    } .flowOn(Dispatchers.IO)


    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService).apply { instance = this }
            }
    }
}