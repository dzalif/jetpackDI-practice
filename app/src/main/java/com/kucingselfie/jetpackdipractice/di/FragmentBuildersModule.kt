package com.kucingselfie.jetpackdipractice.di

import com.kucingselfie.jetpackdipractice.ui.repo.RepoFragment
import com.kucingselfie.jetpackdipractice.ui.search.SearchFragment
import com.kucingselfie.jetpackdipractice.ui.user.UserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): RepoFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment
}