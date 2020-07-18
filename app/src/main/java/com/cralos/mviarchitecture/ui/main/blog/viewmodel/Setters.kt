package com.cralos.mviarchitecture.ui.main.blog.viewmodel

import com.cralos.mviarchitecture.models.BlogPost
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class)
fun BlogViewModel.setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    //if (query.equals(update.blogFields.searchQuery)) {
    //    return
    //}
    update.blogFields.searchQuery = query
    setViewState(update)
}

@OptIn(InternalCoroutinesApi::class)
fun BlogViewModel.setBlogListData(blogList: List<BlogPost>) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.blogList = blogList
    setViewState(update)
}

@OptIn(InternalCoroutinesApi::class)
fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFileds.blogPost = blogPost
    setViewState(update)
}

@OptIn(InternalCoroutinesApi::class)
fun BlogViewModel.setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFileds.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

@OptIn(InternalCoroutinesApi::class)
fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}

@OptIn(InternalCoroutinesApi::class)
fun BlogViewModel.setQueryInProgress(isInProgress: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryInProgress = isInProgress
    setViewState(update)
}