package com.zam.storyapp.data.source.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.zam.storyapp.adapter.paging.StoryPagingSource
import com.zam.storyapp.data.source.model.StoryResponse
import com.zam.storyapp.data.source.model.UserModel
import com.zam.storyapp.data.source.response.*
import com.zam.storyapp.networking.ApiService
import com.zam.storyapp.preference.UserPreferences
import com.zam.storyapp.utils.ResultProcess
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class StoryRepository(private val pref: UserPreferences, private val apiService: ApiService) {
    fun getStory(): LiveData<PagingData<StoryResponse.Story>>{
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                StoryPagingSource(apiService, pref)
            }
        ).liveData
    }

    fun userLogin(email: String, password: String): LiveData<ResultProcess<LoginResponse>> = liveData {
        emit(ResultProcess.Loading)
        try {
            val response = apiService.login(LoginRequest(email, password))
            emit(ResultProcess.Success(response))
        } catch (e: Exception) {
            Log.d("Login", e.message.toString())
            emit(ResultProcess.Error(e.message.toString()))
        }
    }

    fun userRegister(name: String, email: String, password: String)
    : LiveData<ResultProcess<RegisterResponse>> =
        liveData {
        emit(ResultProcess.Loading)
        try {
            val response = apiService.register(
                RegisterRequest(name, email, password)
            )
            emit(ResultProcess.Success(response))
        } catch (e: Exception) {
            Log.d("Signup", e.message.toString())
            emit(ResultProcess.Error(e.message.toString()))
        }
    }

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody, lat: Double?, lon: Double?
    ): LiveData<ResultProcess<AddStoryResponse>> = liveData {
        emit(ResultProcess.Loading)
        try {
            val response = apiService.addStory(token, file, description,lat, lon)
            emit(ResultProcess.Success(response))
        } catch (e: Exception) {
            Log.d("Signup", e.message.toString())
            emit(ResultProcess.Error(e.message.toString()))
        }
    }

    fun getStoryLocation(token: String): LiveData<ResultProcess<StoryResponse>> = liveData {
        emit(ResultProcess.Loading)
        try {
            val response = apiService.getStoryLocation(token, 1)
            emit(ResultProcess.Success(response))
        } catch (e: Exception) {
            Log.d("Signup", e.message.toString())
            emit(ResultProcess.Error(e.message.toString()))
        }
    }

    fun getUserData(): LiveData<UserModel> {
        return pref.getUserData().asLiveData()
    }

    suspend fun saveUserData(user: UserModel) {
        pref.saveUserData(user)
    }

    suspend fun login() {
        pref.login()
    }

    suspend fun logout() {
        pref.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            preferences: UserPreferences,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(preferences, apiService)
            }.also { instance = it }
    }
}

