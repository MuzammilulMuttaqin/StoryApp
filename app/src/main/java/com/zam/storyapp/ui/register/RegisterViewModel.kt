package com.zam.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.zam.storyapp.data.source.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository):ViewModel() {
    fun userRegister(name: String, email: String, password: String) =
        storyRepository.userRegister(name, email, password)
}