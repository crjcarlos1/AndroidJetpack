package com.cralos.mviarchitecture.di.main

import com.cralos.mviarchitecture.api.main.OpenApiMainService
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.persistence.AppDatabase
import com.cralos.mviarchitecture.persistence.BlogPostDao
import com.cralos.mviarchitecture.repository.BlogRepository
import com.cralos.mviarchitecture.repository.main.AccountRepository
import com.cralos.mviarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Retrofit

@InternalCoroutinesApi
@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder.build().create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(
            openApiMainService, accountPropertiesDao, sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db : AppDatabase) : BlogPostDao{
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ) : BlogRepository{
        return BlogRepository(openApiMainService, blogPostDao, sessionManager)
    }

}