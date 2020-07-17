package com.cralos.mviarchitecture.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.cralos.mviarchitecture.api.main.OpenApiMainService
import com.cralos.mviarchitecture.api.main.responses.BlogListSearchResponse
import com.cralos.mviarchitecture.models.AuthToken
import com.cralos.mviarchitecture.models.BlogPost
import com.cralos.mviarchitecture.persistence.BlogPostDao
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.main.blog.state.BlogViewState
import com.cralos.mviarchitecture.util.DateUtils
import com.cralos.mviarchitecture.util.GenericApiResponse
import kotlinx.coroutines.*
import javax.inject.Inject

@InternalCoroutinesApi
class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {
    private val TAG = "AppDebug"

    fun searchBlogRepository(
        authToken: AuthToken,
        query: String
    ): LiveData<DataState<BlogViewState>> {

        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true, false, true
        ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main) {
                    //finish by viewing the db cache
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<BlogListSearchResponse>) {
                val blogPostList = ArrayList<BlogPost>()
                for (blogPostResponse in response.body.results) {
                    blogPostList.add(
                        BlogPost(
                            pk = blogPostResponse.pk,
                            title = blogPostResponse.title,
                            slug = blogPostResponse.slug,
                            body = blogPostResponse.body,
                            image = blogPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(blogPostResponse.date_updated),
                            username = blogPostResponse.username
                        )
                    )
                }
                updateLocalDb(blogPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token}",
                    query = query
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.getAllBlogPosts()
                    .switchMap {
                        object : LiveData<BlogViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(BlogViewState.BlogFields(blogList = it))
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                if (cacheObject != null) {
                    withContext(Dispatchers.IO) {
                        for (blogPost in cacheObject) {
                            try {
                                //launch each insert as a separate job to executed in parallel
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting blog: $blogPost")
                                    blogPostDao.insert(blogPost)
                                }

                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "updateLocalDb: error updating cache on blog post with slug: ${blogPost.slug} "
                                    //optional error handling
                                )
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogRepository", job)
            }
        }.asLiveData()

    }


}