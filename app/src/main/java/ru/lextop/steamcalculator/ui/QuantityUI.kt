package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.*
import ru.lextop.steamcalculator.MainActivity
import ru.lextop.steamcalculator.vm.QuantityViewModel
import ru.lextop.steamcalculator.binding.*

class QuantityUI : Binding.Component<QuantityViewModel, ViewGroup>() {
    override fun Binding<QuantityViewModel>.createView(bindingLo: LifecycleOwner, ui: AnkoContext<ViewGroup>) = with(ui) {
        with(bindingLo) {
            ui.verticalLayout {
                lparams(matchParent, wrapContent)

                textView {
                    bind({ text = spannedFromHtml(it!!) }) { propName }
                }.lparams(matchParent, wrapContent) {
                    marginStart = dip(16)
                }
                linearLayout {
                    val selectPropWidth = dip(48) + sp(32)
                    val smargin = dip(16)
                    val editTextWeight = 1.0f
                    val selectUnitWidth = dip(48) + sp(96)
                    textView {
                        bind({ text = spannedFromHtml(it!!) }) { propSymbol }
                    }.lparams(selectPropWidth, wrapContent){
                        marginStart = smargin
                    }
                    textView {
                        bindLive(this::setText) { valueLive }
                    }.lparams(0, wrapContent, 1f)
                    spinner {
                        bind({ adapter = HtmlArrayAdapter(ctx, it!!) }) { units }
                        bindLive({ setSelection(it!!) }) { unitSelectionLive }
                        onItemSelectedListener = OnItemSelectedListener { notify { selectUnit(it) } }
                    }.lparams(selectUnitWidth, wrapContent)
                }
            }
        }

    }
}