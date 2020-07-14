package com.cralos.mviarchitecture.di.main

import com.cralos.mviarchitecture.ui.main.account.AccountFragment
import com.cralos.mviarchitecture.ui.main.account.ChangePasswordFragment
import com.cralos.mviarchitecture.ui.main.account.UpdateAccountFragment
import com.cralos.mviarchitecture.ui.main.blog.BlogFragment
import com.cralos.mviarchitecture.ui.main.blog.UpdateBlogFragment
import com.cralos.mviarchitecture.ui.main.blog.ViewBlogFragment
import com.cralos.mviarchitecture.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}