package com.zam.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.zam.storyapp.data.source.repository.StoryRepository
import com.zam.storyapp.networking.ApiConfig
import com.zam.storyapp.preference.UserPreferences

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("storyapp")
object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val preferences = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(preferences, apiService)
    }

}