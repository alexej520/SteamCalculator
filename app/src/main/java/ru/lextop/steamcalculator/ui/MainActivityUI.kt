package ru.lextop.steamcalculator.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import ru.lextop.steamcalculator.*
import ru.lextop.steamcalculator.binding.*
import ru.lextop.steamcalculator.vm.QuantityViewModel
import ru.lextop.steamcalculator.vm.SteamViewModel

class MainActivityUI : Binding.Component<SteamViewModel, MainActivity>() {

    override fun Binding<SteamViewModel>.createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        with(ui.owner) {
            verticalLayout {
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    spinner {
                        bind({ adapter = HtmlArrayAdapter(ctx, it!!) }) { firstPropSymbols }
                        bindLive({ setSelection(it!!) }) { firstPropSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectFirstProp(it) } }
                    }.lparams(0, wrapContent, 1.5f)
                    editText {
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
                    }.lparams(0, wrapContent, 1f)
                    spinner {
                        bindLive({ adapter = HtmlArrayAdapter(ctx, it!!) }) { firstUnitsLive }
                        bindLive({ setSelection(it!!) }) { firstUnitSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectFirstUnit(it) } }
                    }.lparams(0, wrapContent, 1.0f)
                }.lparams(matchParent, wrapContent)
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    spinner {
                        bindLive({ adapter = HtmlArrayAdapter(ctx, it!!) }) { secondPropSymbolsLive }
                        bindLive({ setSelection(it!!) }) { secondPropSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectSecondProp(it) } }
                    }.lparams(0, wrapContent, 1.2f)
                    editText {
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
                    }.lparams(0, wrapContent, 1f)
                    spinner {
                        bindLive({ adapter = HtmlArrayAdapter(ctx, it!!) }) { secondUnitsLive }
                        bindLive({ setSelection(it!!) }) { secondUnitSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectSecondUnit(it) } }
                    }.lparams(0, wrapContent, 0.8f)
                }
                recyclerView {
                    layoutManager = LinearLayoutManager(ctx)
                    adapter = SimpleBindingAdapter(owner, QuantityUI())
                    @Suppress("UNCHECKED_CAST")
                    bind({ (adapter as SimpleBindingAdapter<QuantityViewModel, MainActivity>).viewModels = it!! }) { quantityModels }
                }
            }
        }
    }
}