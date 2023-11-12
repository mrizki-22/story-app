package com.example.storyapp.data

import com.example.storyapp.data.local.datastore.UserPreference
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.DataStatus
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepository private constructor(
    private val apiService: ApiService,
    private val pref: UserPreference
) {

    suspend fun register(name: String, email: String, password: String) =  flow {
        emit(DataStatus.loading())
        val result = apiService.register(name, email, password)
        when(result.code()) {
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

    suspend fun login(email: String, password: String) = flow {
        emit(DataStatus.loading())
        val result = apiService.login(email, password)
        when(result.code()) {
            200 -> {
                val token = result.body()?.loginResult?.token
                token?.let { setAuthToken(it) }
                emit(DataStatus.success(result.body()))
            }
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


    private suspend fun setAuthToken(token: String) {
        pref.setToken(token)

    }

    fun getAuthToken(): Flow<String> {
        return pref.getToken()
    }

    suspend fun logout() {
        pref.clearToken()
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, preference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, preference).apply { instance = this }
            }
    }
}