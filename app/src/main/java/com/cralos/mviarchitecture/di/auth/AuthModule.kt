package com.cralos.mviarchitecture.di.auth

import com.cralos.mviarchitecture.api.auth.OpenApiAuthService
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.persistence.AuthTokenDao
import com.cralos.mviarchitecture.repository.auth.AuthRepository
import com.cralos.mviarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Retrofit

@InternalCoroutinesApi
@Module
class AuthModule {

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}