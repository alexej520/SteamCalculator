package ru.lextop.steamcalculator.ui

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.binding.*
import ru.lextop.steamcalculator.vm.RefractiveIndexViewModel

class RefractiveIndexUI : Binding.Component<RefractiveIndexViewModel, ViewGroup>() {
    val riUI = QuantityUI()
    override fun createBinding(ui: AnkoContext<ViewGroup>): Binding<RefractiveIndexViewModel> {
        val binding = Binding<RefractiveIndexViewModel>()
        val refractiveBinding = riUI.createBinding(AnkoContext.create(ui.ctx, ui.owner))
        binding.nested.put(RefractiveIndexViewModel::refractiveIndexQuantityValueViewModel, refractiveBinding)
        with(ui){
            verticalLayout {
                lparams(matchParent, wrapContent)
                with(binding){
                    verticalLayout{
                        startPadding = dip(8)
                        textCaption {
                            startPadding = dip(8)
                            bindLive({ visibility = it!!.toVisibleOrGone() }) { isQuantityNameVisibleLive }
                            bind(this::setText) { ctx.getSpanned(WavelengthWrapper.nameRes) }
                        }.lparams(matchParent, wrapContent)
                        linearLayout {
                            textSubheading {
                                verticalPadding = (textSize / 4).toInt()
                                R.style.Widget_AppCompat_Spinner
                                setEms(2)
                                bind(this::setText) { ctx.getSpanned(WavelengthWrapper.symbolRes) }
                            }.lparams(wrapContent, wrapContent) {
                                horizontalMargin = dip(8)
                                gravity = Gravity.CENTER
                            }
                            editTextMaterial {
                                val listener = object : TextWatcher {
                                    var isEnabled = true
                                    override fun afterTextChanged(input: Editable) {
                                        if (!isEnabled) return
                                        callback { inputWavelengthValue(input) }
                                    }

                                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                                }
                                /*bindLive({
                                    if (it!!) {
                                        requestFocus()
                                        (ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                                .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                                    }
                                }) { wavelengthInputFocusLive }*/
                                bindLive({
                                    listener.isEnabled = false
                                    if (text != it) {
                                        setText(it)
                                        setSelection(length())
                                    }
                                    listener.isEnabled = true
                                }) { wavelengthValueLive }
                                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                                addTextChangedListener(listener)
                            }.lparams(0, wrapContent, 1f)
                            spinner {
                                adapter = SimpleArrayAdapter(ctx)
                                bind({ (adapter as SimpleArrayAdapter).items = it!! }) { wavelengthUnits }
                                bindLive({ setSelection(it!!) }) { wavelengthUnitSelectionLive }
                                onItemSelectedListener = OnItemSelectedListener { callback { selectWavelengthUnit(it) } }
                            }.lparams(wrapContent, wrapContent)
                        }.lparams(matchParent, wrapContent)
                    }
                }
                ankoView({ refractiveBinding.view }, 0, {}).lparams(matchParent, wrapContent)
                binding.setView(this)
            }
        }
        return binding
    }
}