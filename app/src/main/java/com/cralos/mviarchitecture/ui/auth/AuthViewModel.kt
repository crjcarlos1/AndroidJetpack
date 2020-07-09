package com.cralos.mviarchitecture.ui.auth

import androidx.lifecycle.ViewModel
import com.cralos.mviarchitecture.repository.auth.AuthRepository

class AuthViewModel
constructor(
    val authRepository: AuthRepository
) : ViewModel(){}