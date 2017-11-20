package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.view.ViewGroup
import org.jetbrains.anko.*
import ru.lextop.steamcalculator.binding.*
import ru.lextop.steamcalculator.vm.QuantityViewModel

class QuantityUI : Binding.Component<QuantityViewModel, ViewGroup>() {
    override fun Binding<QuantityViewModel>.createView(bindingLo: LifecycleOwner, ui: AnkoContext<ViewGroup>) = with(ui) {
        with(bindingLo) {
            verticalLayout {
                startPadding = dip(8)
                lparams(matchParent, wrapContent)
                val editTextWeight = 1.0f
                val unitSpinnerWeight = 1.0f
                textCaption {
                    startPadding = dip(8)
                    bindLive({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisibleLive }
                    bind(this::setText) { propName }
                }.lparams(matchParent, wrapContent)
                linearLayout {
                    textSubheading {
                        setEms(2)
                        bind(this::setText) { propSymbol }
                    }.lparams(wrapContent, wrapContent) {
                        horizontalMargin = dip(8)
                    }
                    textBody1 {
                        horizontalPadding = dip(8)
                        bindLive(this::setText) { valueLive }
                    }.lparams(0, wrapContent, editTextWeight)
                    spinner {
                        adapter = SimpleArrayAdapter(ctx)
                        bind({ (adapter as SimpleArrayAdapter).items = it!! }) { units }
                        bindLive({ setSelection(it!!) }) { unitSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectUnit(it) } }
                    }.lparams(0, wrapContent, unitSpinnerWeight)
                }
            }
        }

    }
}