package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import org.jetbrains.anko.*
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
                verticalLayout {
                    startPadding = dip(8)
                    val propSpinnerWeight = 0.5f
                    val editTextWeight = 1.0f
                    val unitSpinnerWeight = 1.0f
                    textCaption {
                        bindLive({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisibleLive }
                        bindLive(this::setText) { firstPropNameLive }
                        startPadding = dip(8)
                    }.lparams(matchParent, wrapContent)
                    linearLayout {
                        spinner {
                            adapter = CharSequensePairArrayAdapter(ctx, isSecondVisible = false)
                            bind({ (adapter as CharSequensePairArrayAdapter).items = it!! }) { firstPropNameToSymbolList }
                            bindLive({ (adapter as CharSequensePairArrayAdapter).isSecondInDropdownVisible = it!! }) { isPropNameVisibleLive }
                            bindLive({ setSelection(it!!) }) { firstPropSelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { notify { selectFirstProp(it) } }
                        }.lparams(0, wrapContent, propSpinnerWeight)
                        editTextCompat {
                            val listener = object : TextWatcher {
                                override fun afterTextChanged(input: Editable) {
                                    notify { inputFirstPropValue(input) }
                                }

                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            }
                            bindLive({
                                requestFocus()
                                (ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                            }) { firstInputFocusLive }
                            bindLive({
                                removeTextChangedListener(listener)
                                setText(it)
                                setSelection(length())
                                addTextChangedListener(listener)
                            }) { firstValueLive }
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                            addTextChangedListener(listener)
                        }.lparams(0, wrapContent, editTextWeight)
                        spinner {
                            adapter = CharSequenceArrayAdapter(ctx)
                            bindLive({ (adapter as CharSequenceArrayAdapter).items = it!! }) { firstUnitsLive }
                            bindLive({ setSelection(it!!) }) { firstUnitSelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { notify { selectFirstUnit(it) } }
                        }.lparams(0, wrapContent, unitSpinnerWeight)
                    }.lparams(matchParent, wrapContent)
                    textCaption {
                        startPadding = dip(8)
                        bindLive({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisibleLive }
                        bindLive(this::setText) { secondPropNameLive }
                    }.lparams(matchParent, wrapContent)
                    linearLayout {
                        spinner {
                            adapter = CharSequensePairArrayAdapter(ctx, isSecondVisible = false)
                            bindLive({ (adapter as CharSequensePairArrayAdapter).items = it!! }) { secondPropNameToSymbolListLive }
                            bindLive({ (adapter as CharSequensePairArrayAdapter).isSecondInDropdownVisible = it!! }) { isPropNameVisibleLive }
                            bindLive({ setSelection(it!!) }) { secondPropSelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { notify { selectSecondProp(it) } }
                        }.lparams(0, wrapContent, propSpinnerWeight)
                        editTextCompat {
                            val listener = object : TextWatcher {
                                override fun afterTextChanged(input: Editable) {
                                    notify { inputSecondPropValue(input) }
                                }

                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            }
                            bindLive({
                                requestFocus()
                                (ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                            }) { secondInputFocusLive }
                            bindLive({
                                removeTextChangedListener(listener)
                                setText(it)
                                setSelection(length())
                                addTextChangedListener(listener)
                            }) { secondValueLive }
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                            addTextChangedListener(listener)
                        }.lparams(0, wrapContent, editTextWeight)
                        spinner {
                            adapter = CharSequenceArrayAdapter(ctx)
                            bindLive({ (adapter as CharSequenceArrayAdapter).items = it!! }) { secondUnitsLive }
                            bindLive({ setSelection(it!!) }) { secondUnitSelectionLive }
                            onItemSelectedListener = OnItemSelectedListener { notify { selectSecondUnit(it) } }
                        }.lparams(0, wrapContent, unitSpinnerWeight)
                    }.lparams(matchParent, wrapContent) { verticalPadding = dip(8) }
                }
                view {
                    backgroundDrawable = ctx.getDrawable(android.R.drawable.divider_horizontal_bright)
                }.lparams(matchParent, dip(2))
                themedRecyclerView(R.style.ScrollbarRecyclerView) {
                    scrollBarStyle = View.SCROLLBARS_OUTSIDE_INSET
                    isFocusableInTouchMode = true
                    val lm = LinearLayoutManager(ctx)
                    layoutManager = lm
                    addItemDecoration(DividerItemDecoration(ctx, lm.orientation))
                    adapter = SimpleBindingAdapter(bindingLo, QuantityUI())
                    @Suppress("UNCHECKED_CAST")
                    bind({ (adapter as SimpleBindingAdapter<QuantityViewModel>).viewModels = it!! }) { quantityModels }
                }
            }
        }
    }
}