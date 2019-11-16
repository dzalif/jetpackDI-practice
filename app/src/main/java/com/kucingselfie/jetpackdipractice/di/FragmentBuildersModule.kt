package com.kucingselfie.jetpackdipractice.di

import com.kucingselfie.jetpackdipractice.ui.search.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}