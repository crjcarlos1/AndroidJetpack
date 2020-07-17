package com.cralos.mviarchitecture.ui.main.blog

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.cralos.mviarchitecture.models.BlogPost
import com.cralos.mviarchitecture.repository.BlogRepository
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.ui.BaseViewModel
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.main.blog.state.BlogStateEvent
import com.cralos.mviarchitecture.ui.main.blog.state.BlogViewState
import com.cralos.mviarchitecture.util.AbsentLiveData
import javax.inject.Inject

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
                return AbsentLiveData.create()
            }
            is BlogStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setQuery(query: String) {
        val update = getCurrentViewStateOrNew()
        if (query.equals(update.blogFields.searchQuery)) {
            return
        }
        update.blogFields.searchQuery = query
        _viewState.value = update
    }

    fun setBlogListData(blogList: List<BlogPost>) {
        val update = getCurrentViewStateOrNew()
        update.blogFields.blogList = blogList
        _viewState.value = update
    }

    fun cancelActiveJobs(){
        blogRepositroy.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(BlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}