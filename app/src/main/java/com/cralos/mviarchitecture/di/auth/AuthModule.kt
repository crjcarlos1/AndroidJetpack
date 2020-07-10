package com.cralos.mviarchitecture.di.auth

import com.cralos.mviarchitecture.api.auth.OpenApiAuthService
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.persistence.AuthTokenDao
import com.cralos.mviarchitecture.repository.auth.AuthRepository
import com.cralos.mviarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(): OpenApiAuthService {
        return Retrofit.Builder()
            .baseUrl("https://open-api.xyz")
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