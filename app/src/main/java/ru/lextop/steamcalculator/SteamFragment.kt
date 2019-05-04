package ru.lextop.steamcalculator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import ru.lextop.steamcalculator.binding.browse
import ru.lextop.steamcalculator.binding.email
import ru.lextop.steamcalculator.binding.viewModel
import ru.lextop.steamcalculator.databinding.FragmentSteamBinding
import ru.lextop.steamcalculator.databinding.ItemSelectquantityBinding
import ru.lextop.steamcalculator.di.Injectable
import ru.lextop.steamcalculator.model.DefaultUnits
import ru.lextop.steamcalculator.ui.PropertySpinnerListAdapter
import ru.lextop.steamcalculator.ui.RateView
import ru.lextop.steamcalculator.ui.SteamBindingAdapter
import ru.lextop.steamcalculator.ui.UnitArrayAdapter
import ru.lextop.steamcalculator.vm.RateViewModel
import ru.lextop.steamcalculator.vm.SelectQuantityViewModel
import ru.lextop.steamcalculator.vm.SteamViewModel
import ru.lextop.steamcalculator.vm.ViewModelFactory
import javax.inject.Inject

class SteamFragment : Fragment(), Injectable {
    @Inject
    lateinit var steamViewModelFactory: ViewModelFactory<SteamViewModel>
    @Inject
    lateinit var prefs: SharedPreferences
    @Inject
    lateinit var repo: SteamRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSteamBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
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
                else -> return@setOnMenuItemClickListener false
            }
            true
        }

        val bannerSize = AdSize.SMART_BANNER
        binding.steamAdContainer.minimumHeight = bannerSize.getHeightInPixels(requireContext())
        if (!RateViewModel.mustRate(requireContext())) {
            setAdView(binding.steamAdContainer, bannerSize)
        } else {
            setRateView(binding.steamAdContainer, bannerSize)
        }

        val vm: SteamViewModel = requireActivity().viewModel(steamViewModelFactory)
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

        initSelectedQuantity(vm.firstSelectQuantityViewModel, binding.firstSelectedQuantity)
        initSelectedQuantity(vm.secondSelectQuantityViewModel, binding.secondSelectedQuantity)

        return binding.root
    }

    private fun initSelectedQuantity(sq: SelectQuantityViewModel, b: ItemSelectquantityBinding) {
        b.lifecycleOwner = this
        b.quantityAdapter = PropertySpinnerListAdapter()
        b.unitAdapter = UnitArrayAdapter()

        sq.quantityNameToSymbolListLive.observe(this, Observer {
            b.quantityAdapter!!.submitList(it!!)
        })
        sq.nameVisibleLive.observe(this, Observer {
            b.quantityAdapter!!.isHintVisible = it!!
        })
        sq.unitsLive.observe(this, Observer {
            b.unitAdapter!!.submitList(it!!)
        })
    }

    private fun setAdView(container: FrameLayout, bannerSize: AdSize) {
        val adView = AdView(context!!).apply {
            adSize = bannerSize
            adUnitId = context!!.getString(R.string.adUnitIdBanner)
            loadAd(AdRequest.Builder().build())
        }
        container.removeAllViews()
        container.addView(
            adView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun setRateView(container: FrameLayout, bannerSize: AdSize) {
        val rateView = RateView(ContextThemeWrapper(context!!, R.style.Theme_AppCompat), null, 0)
        RateViewModel.onRateDialogStarted(context!!)
        rateView.onRatedListener = { success, positive ->
            if (success) {
                if (positive) {
                    requireContext().browse(context!!.getString(R.string.contactUsGooglePlay))
                } else {
                    requireContext().email(
                        context!!.getString(R.string.contactUsEmail),
                        subject = context!!.getString(R.string.app_name)
                    )
                }
            }
            rateView.animate()
                .alpha(0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        setAdView(container, bannerSize)
                    }
                })
            RateViewModel.onRateDialogCompleted(context!!, success, positive)
        }
        container.removeAllViews()
        container.addView(
            rateView, FrameLayout.LayoutParams(
                bannerSize.getWidthInPixels(context!!),
                bannerSize.getHeightInPixels(context!!).coerceAtLeast(
                    resources.getDimensionPixelSize(R.dimen.banner_min_height)
                )
            )
        )
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