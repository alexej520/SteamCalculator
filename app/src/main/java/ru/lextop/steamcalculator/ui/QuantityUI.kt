package ru.lextop.steamcalculator.ui

import android.widget.*
import org.jetbrains.anko.*
import ru.lextop.steamcalculator.MainActivity
import ru.lextop.steamcalculator.vm.QuantityViewModel
import ru.lextop.steamcalculator.binding.*

class QuantityUI : Binding.Component<QuantityViewModel, MainActivity>() {
    override fun Binding<QuantityViewModel>.createView(ui: AnkoContext<MainActivity>) = with(ui) {
        with(ui.owner) {
            ui.linearLayout {
                lparams(matchParent, wrapContent)
                orientation = LinearLayout.HORIZONTAL
                textView {
                    bind(this::setText) { propName }
                }.lparams(0, wrapContent, 2f) {
                    marginStart = dip(16)
                }
                textView {
                    bind(this::setText) { propSymbol }
                }.lparams(0, wrapContent, 1f)
                textView {
                    bindLive(this::setText) { valueLive }
                }.lparams(0, wrapContent, 1f)
                spinner {
                    bind({adapter = HtmlArrayAdapter(ctx, it!!)}) { units }
                    bindLive({ setSelection(it!!) }) { unitSelectionLive }
                    onItemSelectedListener = OnItemSelectedListener { notify { selectUnit(it) } }
                }.lparams(0, wrapContent, 2f)
            }
        }

    }
}