package com.cralos.mviarchitecture.ui.main.blog.state

sealed class BlogStateEvent {

    class BlogSearchEvent : BlogStateEvent()

    class CheckAuthorOfBlogPost : BlogStateEvent()

    class None : BlogStateEvent()

}