package ru.lextop.steamcalculator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import org.jetbrains.anko.AnkoContext
import ru.lextop.steamcalculator.binding.viewModel
import ru.lextop.steamcalculator.di.Injectable
import ru.lextop.steamcalculator.ui.SteamUI
import ru.lextop.steamcalculator.vm.ViewModelFactory
import javax.inject.Inject

class SteamFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = SteamUI()
                .createBinding(this, AnkoContext.create(context!!, this))
        binding.setViewModel(this, activity!!.viewModel(viewModelFactory))
        return binding.view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.steam_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SettingsFragment())
                    .addToBackStack("settings")
                    .commit()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}