package ru.lextop.steamcalculator.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Spinner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.SteamFragment
import ru.lextop.steamcalculator.binding.*
import ru.lextop.steamcalculator.databinding.ItemQuantityBinding
import ru.lextop.steamcalculator.databinding.ItemRefractiveindexBinding
import ru.lextop.steamcalculator.model.wrapper
import ru.lextop.steamcalculator.vm.RateViewModel
import ru.lextop.steamcalculator.vm.SteamViewModel
import steam.quantities.RefractiveIndex
import steam.quantities.Wavelength

class SteamUI : Binding.SimpleComponent<SteamViewModel, SteamFragment>() {

    override fun Binding<SteamViewModel>.createView(ui: AnkoContext<SteamFragment>): View = with(ui) {
        verticalLayout root@ {
            lparams(matchParent, matchParent)
            val bannerSize = AdSize.SMART_BANNER
            if (!RateViewModel.mustRate(ctx)) {
                adView {
                    adSize = bannerSize
                    adUnitId = ctx.getString(R.string.adUnitIdBanner)
                    loadAd(AdRequest.Builder().build())
                }.lparams(matchParent, wrapContent)
            } else {
                rateView {
                    RateViewModel.onRateDialogStarted(ctx)
                    onRatedListener = { success, positive ->
                        if (success) {
                            if (positive) {
                                browse(ctx.getString(R.string.contactUsGooglePlay))
                            } else {
                                email(ctx.getString(R.string.contactUsEmail), subject = ctx.getString(R.string.app_name))
                            }
                        }
                        animate()
                                .alpha(0f)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator?) {
                                        val index = (this@root as LinearLayout).indexOfChild(this@rateView)
                                        this@root.removeViewAt(index)
                                        this@root.addView(com.google.android.gms.ads.AdView(this@root.context).apply {
                                            adSize = bannerSize
                                            adUnitId = ctx.getString(R.string.adUnitIdBanner)
                                            loadAd(AdRequest.Builder().build())
                                        }, index, android.widget.LinearLayout.LayoutParams(matchParent, wrapContent))
                                    }
                                })
                        RateViewModel.onRateDialogCompleted(ctx, success, positive)
                    }
                }.lparams(bannerSize.getWidthInPixels(ctx), maxOf(bannerSize.getHeightInPixels(ctx), dip(48)))
            }
            cardView {
                radius = 0f
                elevationCompat = dip(4).toFloat()
                verticalLayout {
                    startPadding = dip(8)
                    textCaption {
                        bindLive({ visibility = it!!.toVisibleOrGone() }) { isQuantityNameVisibleLive }
                        bindLive(this::setText) { firstQuantityNameLive }
                        startPadding = dip(8)
                    }.lparams(matchParent, wrapContent)
                    linearLayout {
                        spinner {
                            adapter = DropdownHintArrayAdapter(ctx)
                            bind({ (adapter as DropdownHintArrayAdapter).items = it!! }) { firstQuantityNameToSymbolList }
                            bindLive({ (adapter as DropdownHintArrayAdapter).isHintVisible = it!! }) { isQuantityNameVisibleLive }
                            bindLive({ setSelection(it!!) }) { firstQuantitySelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { callback { selectFirstQuantity(it) } }
                        }.lparams(wrapContent, wrapContent)
                        editTextMaterial {
                            val listener = object : TextWatcher {
                                var isEnabled = true
                                override fun afterTextChanged(input: Editable) {
                                    if (!isEnabled) return
                                    callback { inputFirstValue(input) }
                                }

                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            }
                            bindLive({
                                if (it!!) {
                                    requestFocus()
                                    (ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                                }
                            }) { firstInputFocusLive }
                            bindLive({
                                listener.isEnabled = false
                                if (text != it){
                                    setText(it)
                                    setSelection(length())
                                }
                                listener.isEnabled = true
                            }) { firstValueLive }
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                            addTextChangedListener(listener)
                        }.lparams(0, wrapContent, 1f)
                        spinner {
                            adapter = UnitArrayAdapter(ctx)
                            bindLive({ (adapter as UnitArrayAdapter).items = it!! }) { firstUnitsLive }
                            bindLive({ setSelection(it!!) }) { firstUnitSelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { callback { selectFirstUnit(it) } }
                        }.lparams(wrapContent, wrapContent)
                    }.lparams(matchParent, wrapContent)
                    textCaption {
                        startPadding = dip(8)
                        bindLive({ visibility = it!!.toVisibleOrGone() }) { isQuantityNameVisibleLive }
                        bindLive(this::setText) { secondQuantityNameLive }
                    }.lparams(matchParent, wrapContent)
                    linearLayout {
                        spinner {
                            adapter = DropdownHintArrayAdapter(ctx)
                            bindLive({ (adapter as DropdownHintArrayAdapter).items = it!! }) { secondQuantityNameToSymbolListLive }
                            bindLive({ (adapter as DropdownHintArrayAdapter).isHintVisible = it!! }) { isQuantityNameVisibleLive }
                            bindLive({ setSelection(it!!) }) { secondQuantitySelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { callback { selectSecondProp(it) } }
                        }.lparams(wrapContent, wrapContent)
                        editTextMaterial {
                            val listener = object : TextWatcher {
                                var isEnabled = true
                                override fun afterTextChanged(input: Editable) {
                                    if (!isEnabled) return
                                    callback { inputSecondValue(input) }
                                }

                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            }
                            bindLive({
                                if (it!!) {
                                    requestFocus()
                                    (ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                                }
                            }) { secondInputFocusLive }
                            bindLive({
                                listener.isEnabled = false
                                if (text != it){
                                    setText(it)
                                    setSelection(length())
                                }
                                listener.isEnabled = true
                            }) { secondValueLive }
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                            addTextChangedListener(listener)
                        }.lparams(0, wrapContent, 1f)
                        spinner {
                            adapter = UnitArrayAdapter(ctx)
                            bindLive({ (adapter as UnitArrayAdapter).items = it!! }) { secondUnitsLive }
                            bindLive({ setSelection(it!!) }) { secondUnitSelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { callback { selectSecondUnit(it) } }
                        }.lparams(wrapContent, wrapContent)
                    }.lparams(matchParent, wrapContent)
                }
            }.lparams(matchParent, wrapContent)
            recyclerView {
                scrollBarStyle = View.SCROLLBARS_OUTSIDE_INSET
                val lm = LinearLayoutManager(ctx)
                layoutManager = lm
                addItemDecoration(DividerItemDecoration(ctx, lm.orientation))
                adapter = SteamBindingAdapter(owner)
                bind({ (adapter as SteamBindingAdapter).viewModels = it!! }) { quantityValueViewModels }
                bind({ (adapter as SteamBindingAdapter).refractiveIndexViewModel = it!! }) { refractiveIndexViewModel }
            }.lparams(matchParent, wrapContent)
        }
    }
}