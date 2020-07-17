package com.cralos.mviarchitecture.repository

import com.cralos.mviarchitecture.api.main.OpenApiMainService
import com.cralos.mviarchitecture.persistence.BlogPostDao
import com.cralos.mviarchitecture.session.SessionManager
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {
    private val TAG = "AppDebug"


}