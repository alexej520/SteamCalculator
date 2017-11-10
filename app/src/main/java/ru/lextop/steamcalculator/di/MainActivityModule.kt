package ru.lextop.steamcalculator.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lextop.steamcalculator.MainActivity

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun mainActivityInjector(): MainActivity
}

