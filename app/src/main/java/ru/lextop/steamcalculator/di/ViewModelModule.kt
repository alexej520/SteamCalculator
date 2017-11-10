package ru.lextop.steamcalculator.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.lextop.steamcalculator.vm.SteamViewModel
import ru.lextop.steamcalculator.vm.ViewModelFactory

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SteamViewModel::class)
    abstract fun bindSteamViewModel(vm: SteamViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}