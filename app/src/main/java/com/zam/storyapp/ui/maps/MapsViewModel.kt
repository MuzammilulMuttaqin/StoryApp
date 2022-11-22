package com.zam.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zam.storyapp.data.source.model.UserModel
import com.zam.storyapp.data.source.repository.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStoryLocation(token: String) =
        repository.getStoryLocation(token)

    fun getUser(): LiveData<UserModel> {
        return repository.getUserData()
    }
}