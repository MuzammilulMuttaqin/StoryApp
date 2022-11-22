package com.zam.storyapp.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zam.storyapp.data.source.model.StoryResponse
import com.zam.storyapp.preference.UserPreferences
import com.zam.storyapp.networking.ApiService
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val apiService: ApiService, private val pref: UserPreferences) : PagingSource<Int, StoryResponse.Story>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse.Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token = "Bearer ${pref.getUserData().first().token}"
            val responseData = apiService.getStory(token, page, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResponse.Story>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
