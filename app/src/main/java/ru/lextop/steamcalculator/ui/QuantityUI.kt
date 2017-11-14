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
            ui.verticalLayout {
                lparams(matchParent, wrapContent)
                textView {
                    bind({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisible }
                    bind(this::setText) { propName }
                }.lparams(matchParent, wrapContent) {
                    marginStart = dip(16)
                }
                linearLayout {
                    val selectPropWidth = dip(48) + sp(32)
                    val smargin = dip(16)
                    val editTextWeight = 1.0f
                    val selectUnitWidth = dip(48) + sp(96)
                    textView {
                        bind(this::setText) { propSymbol }
                    }.lparams(selectPropWidth, wrapContent) {
                        marginStart = smargin
                    }
                    textView {
                        bindLive(this::setText) { valueLive }
                    }.lparams(0, wrapContent, 1f)
                    spinner {
                        adapter = CharSequenceArrayAdapter(ctx)
                        bind({ (adapter as CharSequenceArrayAdapter).items = it!! }) { units }
                        bindLive({ setSelection(it!!) }) { unitSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectUnit(it) } }
                    }.lparams(selectUnitWidth, wrapContent)
                }
            }
        }

    }
}