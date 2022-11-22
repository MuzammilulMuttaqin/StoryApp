package com.zam.storyapp.ui.homelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zam.storyapp.data.source.model.StoryResponse
import com.zam.storyapp.data.source.model.UserModel
import com.zam.storyapp.data.source.repository.StoryRepository

class ListStoryViewModel (private val repository: StoryRepository) : ViewModel(){
    fun getStory(): LiveData<PagingData<StoryResponse.Story>> {
        return  repository.getStory().cachedIn(viewModelScope)
    }

    fun getUser(): LiveData<UserModel> {
        return repository.getUserData()
    }
}