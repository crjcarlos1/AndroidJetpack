package com.cralos.mviarchitecture.di.auth

import com.cralos.mviarchitecture.ui.auth.ForgotPasswordFragment
import com.cralos.mviarchitecture.ui.auth.LauncherFragment
import com.cralos.mviarchitecture.ui.auth.LoginFragment
import com.cralos.mviarchitecture.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Proveedor de fragments
 */
@InternalCoroutinesApi
@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}