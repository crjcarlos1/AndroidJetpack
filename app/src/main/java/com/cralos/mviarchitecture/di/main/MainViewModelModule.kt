package com.cralos.mviarchitecture.di.main

import androidx.lifecycle.ViewModel
import com.cralos.mviarchitecture.di.ViewModelKey
import com.cralos.mviarchitecture.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel

}