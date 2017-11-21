package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.recyclerview.v7.themedRecyclerView
import ru.lextop.steamcalculator.*
import ru.lextop.steamcalculator.binding.*
import ru.lextop.steamcalculator.vm.QuantityViewModel
import ru.lextop.steamcalculator.vm.SteamViewModel

class SteamUI : Binding.Component<SteamViewModel, SteamFragment>() {

    override fun Binding<SteamViewModel>.createView(bindingLo: LifecycleOwner, ui: AnkoContext<SteamFragment>): View = with(ui) {
        with(bindingLo) {
            verticalLayout {
                lparams(matchParent, matchParent)
                adView {
                    adSize = AdSize.SMART_BANNER
                    adUnitId = ctx.getString(R.string.adUnitIdBanner)
                    loadAd(AdRequest.Builder().build())
                }.lparams(matchParent, wrapContent)
                cardView {
                    radius = 0f
                    elevationCompat = dip(4).toFloat()
                    verticalLayout {
                        startPadding = dip(8)
                        textCaption {
                            bindLive({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisibleLive }
                            bindLive(this::setText) { firstPropNameLive }
                            startPadding = dip(8)
                        }.lparams(matchParent, wrapContent)
                        linearLayout {
                            spinner {
                                adapter = DropdownHintArrayAdapter(ctx)
                                bind({ (adapter as DropdownHintArrayAdapter).items = it!! }) { firstPropNameToSymbolList }
                                bindLive({ (adapter as DropdownHintArrayAdapter).isHintVisible = it!! }) { isPropNameVisibleLive }
                                bindLive({ setSelection(it!!) }) { firstPropSelectionLive }
                                onItemSelectedListener = OnItemSelectedListener { notify { selectFirstProp(it) } }
                            }.lparams(wrapContent, wrapContent)
                            editTextMaterial {
                                val listener = object : TextWatcher {
                                    override fun afterTextChanged(input: Editable) {
                                        notify { inputFirstPropValue(input) }
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
                                    removeTextChangedListener(listener)
                                    setText(it)
                                    setSelection(length())
                                    addTextChangedListener(listener)
                                }) { firstValueLive }
                                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                                addTextChangedListener(listener)
                            }.lparams(0, wrapContent, 1f)
                            spinner {
                                adapter = SimpleArrayAdapter(ctx)
                                bindLive({ (adapter as SimpleArrayAdapter).items = it!! }) { firstUnitsLive }
                                bindLive({ setSelection(it!!) }) { firstUnitSelectionLive }
                                onItemSelectedListener = OnItemSelectedListener { notify { selectFirstUnit(it) } }
                            }.lparams(wrapContent, wrapContent)
                        }.lparams(matchParent, wrapContent)
                        textCaption {
                            startPadding = dip(8)
                            bindLive({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisibleLive }
                            bindLive(this::setText) { secondPropNameLive }
                        }.lparams(matchParent, wrapContent)
                        linearLayout {
                            spinner {
                                adapter = DropdownHintArrayAdapter(ctx)
                                bindLive({ (adapter as DropdownHintArrayAdapter).items = it!! }) { secondPropNameToSymbolListLive }
                                bindLive({ (adapter as DropdownHintArrayAdapter).isHintVisible = it!! }) { isPropNameVisibleLive }
                                bindLive({ setSelection(it!!) }) { secondPropSelectionLive }
                                onItemSelectedListener = OnItemSelectedListener { notify { selectSecondProp(it) } }
                            }.lparams(wrapContent, wrapContent)
                            editTextMaterial {
                                val listener = object : TextWatcher {
                                    override fun afterTextChanged(input: Editable) {
                                        notify { inputSecondPropValue(input) }
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
                                    removeTextChangedListener(listener)
                                    setText(it)
                                    setSelection(length())
                                    addTextChangedListener(listener)
                                }) { secondValueLive }
                                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                                addTextChangedListener(listener)
                            }.lparams(0, wrapContent, 1f)
                            spinner {
                                adapter = SimpleArrayAdapter(ctx)
                                bindLive({ (adapter as SimpleArrayAdapter).items = it!! }) { secondUnitsLive }
                                bindLive({ setSelection(it!!) }) { secondUnitSelectionLive }
                                onItemSelectedListener = OnItemSelectedListener { notify { selectSecondUnit(it) } }
                            }.lparams(wrapContent, wrapContent)
                        }.lparams(matchParent, wrapContent)
                    }
                }.lparams(matchParent, wrapContent)
                recyclerView {
                    scrollBarStyle = View.SCROLLBARS_OUTSIDE_INSET
                    val lm = LinearLayoutManager(ctx)
                    layoutManager = lm
                    addItemDecoration(DividerItemDecoration(ctx, lm.orientation))
                    adapter = SimpleBindingAdapter(bindingLo, QuantityUI())
                    @Suppress("UNCHECKED_CAST")
                    bind({ (adapter as SimpleBindingAdapter<QuantityViewModel>).viewModels = it!! }) { quantityModels }
                }.lparams(matchParent, wrapContent)
            }
        }
    }
}