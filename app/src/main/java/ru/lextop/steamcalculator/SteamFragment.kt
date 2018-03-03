package ru.lextop.steamcalculator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import ru.lextop.steamcalculator.binding.viewModel
import ru.lextop.steamcalculator.databinding.FragmentSteamBinding
import ru.lextop.steamcalculator.di.Injectable
import ru.lextop.steamcalculator.model.DefaultUnits
import android.arch.lifecycle.Observer
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.FrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.browse
import org.jetbrains.anko.support.v4.email
import ru.lextop.steamcalculator.binding.rateView
import ru.lextop.steamcalculator.ui.*
import ru.lextop.steamcalculator.vm.RateViewModel
import ru.lextop.steamcalculator.vm.SteamViewModel
import ru.lextop.steamcalculator.vm.ViewModelFactory
import javax.inject.Inject

class SteamFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var prefs: SharedPreferences
    @Inject
    lateinit var repo: SteamRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ab = (activity!! as AppCompatActivity).supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(false)
        ab.setTitle(R.string.app_name)
        val binding = FragmentSteamBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.firstSelectedQuantity.setLifecycleOwner(this)
        binding.secondSelectedQuantity.setLifecycleOwner(this)

        val bannerSize = AdSize.SMART_BANNER
        if (!RateViewModel.mustRate(context!!)) {
            setAdView(binding, bannerSize)
        } else {
            setRateView(binding, bannerSize)
        }

        val vm: SteamViewModel = activity!!.viewModel(viewModelFactory)
        binding.vm = vm
        binding.steamQuantityValues.adapter = SteamBindingAdapter(this).apply {
            viewModels = vm.quantityValueViewModels
            refractiveIndexViewModel = vm.refractiveIndexViewModel
        }
        binding.steamQuantityValues.addItemDecoration(
            DividerItemDecoration(
                context!!,
                LinearLayoutManager.VERTICAL
            )
        )

        binding.firstSelectedQuantity.quantityAdapter = DropdownHintArrayAdapter(context!!)
        binding.firstSelectedQuantity.unitAdapter = UnitArrayAdapter(context!!)
        vm.firstSelectQuantityViewModel.quantityNameToSymbolListLive.observe(this, Observer {
            binding.firstSelectedQuantity.quantityAdapter!!.items = it!!
        })
        vm.firstSelectQuantityViewModel.unitsLive.observe(this, Observer {
            binding.firstSelectedQuantity.unitAdapter!!.items = it!!
        })

        binding.secondSelectedQuantity.quantityAdapter = DropdownHintArrayAdapter(context!!)
        binding.secondSelectedQuantity.unitAdapter = UnitArrayAdapter(context!!)
        vm.secondSelectQuantityViewModel.quantityNameToSymbolListLive.observe(this, Observer {
            binding.secondSelectedQuantity.quantityAdapter!!.items = it!!
        })
        vm.secondSelectQuantityViewModel.unitsLive.observe(this, Observer {
            binding.secondSelectedQuantity.unitAdapter!!.items = it!!
        })

        return binding.root
    }

    private fun setAdView(binding: FragmentSteamBinding, bannerSize: AdSize) {
        val adView = AdView(context!!).apply {
            adSize = bannerSize
            adUnitId = context!!.getString(R.string.adUnitIdBanner)
            loadAd(AdRequest.Builder().build())
        }
        binding.steamAdContainer.removeAllViews()
        binding.steamAdContainer.addView(
            adView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun setRateView(binding: FragmentSteamBinding, bannerSize: AdSize) {
        val rateView =
            RateView(ContextThemeWrapper(context!!, R.style.PreferenceFixTheme), null, 0).apply {
                RateViewModel.onRateDialogStarted(context!!)
                onRatedListener = { success, positive ->
                    if (success) {
                        if (positive) {
                            browse(context!!.getString(R.string.contactUsGooglePlay))
                        } else {
                            email(
                                context!!.getString(R.string.contactUsEmail),
                                subject = context!!.getString(R.string.app_name)
                            )
                        }
                    }
                    animate()
                        .alpha(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                setAdView(binding, bannerSize)
                            }
                        })
                    RateViewModel.onRateDialogCompleted(context!!, success, positive)
                }
            }
        binding.steamAdContainer.removeAllViews()
        binding.steamAdContainer.addView(
            rateView, FrameLayout.LayoutParams(
                bannerSize.getWidthInPixels(context!!),
                maxOf(bannerSize.getHeightInPixels(context!!), context!!.dip(48))
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.steam_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_preferences -> {
                setActualUnitSetPreference()
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SettingsFragment())
                    .addToBackStack("settings")
                    .commit()
            }
            R.id.menu_info_details -> activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, InfoDetailsFragment())
                .addToBackStack("info_details")
                .commit()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setActualUnitSetPreference() {
        val context = context!!
        val unitSetKey = context.getString(R.string.preferenceKeyUnitSet)

        val unitSystem = DefaultUnits.allUnitSystems.firstOrNull {
            repo.editUnits.all { (q, u) ->
                u.value == it(q.defaultUnits)
            } && repo.viewUnits.all { (q, u) ->
                u.value == it(q.defaultUnits)
            }
        }
        val unitSystemName = DefaultUnits.getName(context, unitSystem)

        if (unitSystemName != prefs.getString(unitSetKey, "")) {
            prefs.edit().putString(unitSetKey, unitSystemName).apply()
        }
    }
}