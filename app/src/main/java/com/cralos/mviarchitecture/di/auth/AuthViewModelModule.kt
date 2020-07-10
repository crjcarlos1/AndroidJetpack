package com.cralos.mviarchitecture.di.auth

import androidx.lifecycle.ViewModel
import com.cralos.mviarchitecture.di.ViewModelKey
import com.cralos.mviarchitecture.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}