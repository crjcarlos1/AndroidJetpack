package com.cralos.mviarchitecture.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.cralos.mviarchitecture.repository.BlogRepository
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.ui.BaseViewModel
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.Loading
import com.cralos.mviarchitecture.ui.main.blog.state.BlogStateEvent
import com.cralos.mviarchitecture.ui.main.blog.state.BlogViewState
import com.cralos.mviarchitecture.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepositroy: BlogRepository,
    private val sharePreferences: SharedPreferences,
    private val requestManager: RequestManager //Glide
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepositroy.searchBlogRepository(
                        authToken = authToken,
                        query = getSearchQuery(),
                        page = getPage()
                    )
                } ?: AbsentLiveData.create()
            }
            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                return AbsentLiveData.create()
            }
            is BlogStateEvent.None -> {
                return object : LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    fun cancelActiveJobs() {
        blogRepositroy.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData() {
        setStateEvent(BlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}