package com.zam.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zam.storyapp.data.source.model.UserModel
import com.zam.storyapp.data.source.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel(){
    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?,
                 lon: Double?) =
        storyRepository.addStory(token, file, description, lat, lon)

    fun getUser(): LiveData<UserModel> {
        return storyRepository.getUserData()
    }
}