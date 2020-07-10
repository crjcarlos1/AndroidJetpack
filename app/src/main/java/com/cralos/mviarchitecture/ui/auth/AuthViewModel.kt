package com.cralos.mviarchitecture.ui.auth

import androidx.lifecycle.ViewModel
import com.cralos.mviarchitecture.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel(){}