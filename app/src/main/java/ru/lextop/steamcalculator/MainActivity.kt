package ru.lextop.steamcalculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.*
import ru.lextop.steamcalculator.binding.Binding
import ru.lextop.steamcalculator.binding.getBinding
import ru.lextop.steamcalculator.binding.viewModel
import ru.lextop.steamcalculator.di.Injectable
import ru.lextop.steamcalculator.ui.MainActivityUI
import ru.lextop.steamcalculator.vm.SteamViewModel
import ru.lextop.steamcalculator.vm.ViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainActivityUI()
                .createBinding(this, AnkoContext.Companion.create(this, this))
        binding.setViewModel(this, viewModel(viewModelFactory))
        setContentView(binding.view)
    }
}