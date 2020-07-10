package com.cralos.mviarchitecture.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.cralos.mviarchitecture.api.auth.network_responses.LoginResponse
import com.cralos.mviarchitecture.api.auth.network_responses.RegistrationResponse
import com.cralos.mviarchitecture.repository.auth.AuthRepository
import com.cralos.mviarchitecture.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

    fun testLogin(): LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testLoginResquest("crjcarlos.1@gmail.com", "logitech1991")
    }

    fun testRegister() : LiveData<GenericApiResponse<RegistrationResponse>>{
        return authRepository.testRegistrationRequest("crjcarlos.1@gmail.com","carlos","logitect1991","logitech1991 ")
    }

}