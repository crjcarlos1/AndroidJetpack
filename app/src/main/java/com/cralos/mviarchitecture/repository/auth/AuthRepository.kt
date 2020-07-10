package com.cralos.mviarchitecture.repository.auth

import androidx.lifecycle.LiveData
import com.cralos.mviarchitecture.api.auth.OpenApiAuthService
import com.cralos.mviarchitecture.api.auth.network_responses.LoginResponse
import com.cralos.mviarchitecture.api.auth.network_responses.RegistrationResponse
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.persistence.AuthTokenDao
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    fun testLoginResquest(
        email : String,
        password : String
    ) : LiveData<GenericApiResponse<LoginResponse>>{
        return openApiAuthService.login(email, password)
    }

    fun testRegistrationRequest(
        email : String,
        username: String,
        password : String,
        confirmPassword : String
    ) : LiveData<GenericApiResponse<RegistrationResponse>>{
        return openApiAuthService.register(email,username,password,confirmPassword)
    }

}