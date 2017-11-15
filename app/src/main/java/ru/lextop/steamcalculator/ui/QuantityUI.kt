package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import ru.lextop.steamcalculator.vm.QuantityViewModel
import ru.lextop.steamcalculator.binding.*

class QuantityUI : Binding.Component<QuantityViewModel, ViewGroup>() {
    override fun Binding<QuantityViewModel>.createView(bindingLo: LifecycleOwner, ui: AnkoContext<ViewGroup>) = with(ui) {
        with(bindingLo) {
            verticalLayout {
                startPadding = dip(8)
                lparams(matchParent, wrapContent)
                val propSpinnerWeight = 0.5f
                val editTextWeight = 1.0f
                val unitSpinnerWeight = 1.0f
                textCaption {
                    startPadding = dip(8)
                    bindLive({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisibleLive }
                    bind(this::setText) { propName }
                }.lparams(matchParent, wrapContent)
                linearLayout {
                    textBody1 {
                        startPadding = dip(8)
                        verticalPadding = dip(8)
                        bind(this::setText) { propSymbol }
                    }.lparams(0, wrapContent, propSpinnerWeight)
                    textBody1 {
                        bindLive(this::setText) { valueLive }
                    }.lparams(0, wrapContent, editTextWeight)
                    spinner {
                        adapter = CharSequenceArrayAdapter(ctx)
                        bind({ (adapter as CharSequenceArrayAdapter).items = it!! }) { units }
                        bindLive({ setSelection(it!!) }) { unitSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectUnit(it) } }
                    }.lparams(0, wrapContent, unitSpinnerWeight)
                }
            }
        }

    }
}