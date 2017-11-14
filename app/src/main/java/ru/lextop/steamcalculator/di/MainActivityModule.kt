package ru.lextop.steamcalculator.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lextop.steamcalculator.MainActivity
import ru.lextop.steamcalculator.SteamFragment

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun mainActivityInjector(): MainActivity

    @ContributesAndroidInjector
    abstract fun steamFrgmentInjector(): SteamFragment
}

