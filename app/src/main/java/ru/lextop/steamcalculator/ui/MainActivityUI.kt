package ru.lextop.steamcalculator.ui

import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
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
                        adapter = PropertyArrayAdapter(ctx)
                        bind({ (adapter as PropertyArrayAdapter).properties = it }) { firstProps }
                        bindLive({ setSelection(it!!) }) { firstPropSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectFirstProp(it) } }
                    }.lparams(0, wrapContent, 1.2f)
                    editText {
                        val listener = object : TextWatcher {
                            override fun afterTextChanged(input: Editable) {
                                notify { inputFirstPropValue(input) }
                            }

                            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        }
                        bindLive({ requestFocus() }) { firstInputFocusLive }
                        bindLive({
                            removeTextChangedListener(listener)
                            setText(it)
                            addTextChangedListener(listener)
                        }) { firstValueLive }
                        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                        addTextChangedListener(listener)
                    }.lparams(0, wrapContent, 1f)
                    spinner {
                        bindLive({ adapter = HtmlArrayAdapter(ctx, android.R.layout.simple_spinner_item, it!!) }) { firstUnitsLive }
                        bindLive({ setSelection(it!!) }) { firstUnitSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectFirstUnit(it) } }
                    }.lparams(0, wrapContent, 0.8f)
                }.lparams(matchParent, wrapContent)
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    spinner {
                        adapter = PropertyArrayAdapter(ctx)
                        bindLive({ (adapter as PropertyArrayAdapter).properties = it }) { secondPropsLive }
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
                        bindLive({ requestFocus() }) { secondInputFocusLive }
                        bindLive({
                            removeTextChangedListener(listener)
                            setText(it)
                            addTextChangedListener(listener)
                        }) { secondValueLive }
                        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                        addTextChangedListener(listener)
                    }.lparams(0, wrapContent, 1f)
                    spinner {
                        bindLive({ adapter = HtmlArrayAdapter(ctx, android.R.layout.simple_spinner_item, it!!) }) { secondUnitsLive }
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