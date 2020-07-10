package com.cralos.mviarchitecture.di

import androidx.lifecycle.ViewModelProvider
import com.cralos.mviarchitecture.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

/**
 * Generador de view models
 */
@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}