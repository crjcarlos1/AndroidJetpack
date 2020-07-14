package com.cralos.mviarchitecture.di

import com.cralos.mviarchitecture.di.auth.AuthFragmentBuildersModule
import com.cralos.mviarchitecture.di.auth.AuthModule
import com.cralos.mviarchitecture.di.auth.AuthScope
import com.cralos.mviarchitecture.di.auth.AuthViewModelModule
import com.cralos.mviarchitecture.di.main.MainFragmentBuildersModule
import com.cralos.mviarchitecture.di.main.MainModule
import com.cralos.mviarchitecture.di.main.MainScope
import com.cralos.mviarchitecture.di.main.MainViewModelModule
import com.cralos.mviarchitecture.ui.auth.AuthActivity
import com.cralos.mviarchitecture.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}