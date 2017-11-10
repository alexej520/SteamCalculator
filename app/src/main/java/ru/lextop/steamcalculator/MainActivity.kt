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
    var binding: Binding<SteamViewModel>? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*object : AnkoComponent<MainActivity>{
            override fun createView(ui: AnkoContext<MainActivity>): View {
             return   ui.linearLayout {
                    textView("kfjl")
                }
            }
        }.setContentView(this)*/
        binding = MainActivityUI().createBinding(AnkoContext.create(this, this)).view.getBinding()
             /*   .setContentView(this)
                .getBinding<SteamViewModel>()*/
        binding!!.setViewModel(this, viewModel(viewModelFactory))
    }

    override fun onDestroy() {
        super.onDestroy()
        //binding!!.setViewModel(null , null)
        //binding = null
        //App.refWatcher.watch(this)
    }
}