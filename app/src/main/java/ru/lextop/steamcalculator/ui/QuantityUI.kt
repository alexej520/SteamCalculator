package ru.lextop.steamcalculator.ui

import android.view.Gravity
import android.view.ViewGroup
import org.jetbrains.anko.*
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.binding.*
import ru.lextop.steamcalculator.vm.QuantityValueViewModel

class QuantityUI : Binding.Component<QuantityValueViewModel, ViewGroup>() {
    override fun Binding<QuantityValueViewModel>.createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        verticalLayout {
            startPadding = dip(8)
            lparams(matchParent, wrapContent)
            textCaption {
                startPadding = dip(8)
                bindLive({ visibility = it!!.toVisibleOrGone() }) { isPropNameVisibleLive }
                bind(this::setText) { propName }
            }.lparams(matchParent, wrapContent)
            linearLayout {
                isBaselineAligned = false
                textSubheading {
                    verticalPadding = (textSize / 4).toInt()
                    R.style.Widget_AppCompat_Spinner
                    setEms(2)
                    bind(this::setText) { propSymbol }
                }.lparams(wrapContent, wrapContent) {
                    horizontalMargin = dip(8)
                    gravity = Gravity.CENTER
                }
                textBody1 {
                    endPadding = dip(8)
                    bindLive(this::setText) { valueLive }
                }.lparams(0, wrapContent, 1f) {
                    gravity = Gravity.CENTER
                }
                spinner {
                    adapter = SimpleArrayAdapter(ctx)
                    bind({ (adapter as SimpleArrayAdapter).items = it!! }) { units }
                    bindLive({ setSelection(it!!) }) { unitSelectionLive }
                    onItemSelectedListener = OnItemSelectedListener { callback { selectUnit(it) } }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.CENTER
                }
            }
        }

    }
}