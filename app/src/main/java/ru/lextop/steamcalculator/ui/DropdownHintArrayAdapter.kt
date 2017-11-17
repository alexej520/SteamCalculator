package ru.lextop.steamcalculator.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import ru.lextop.steamcalculator.binding.endPadding
import ru.lextop.steamcalculator.binding.textCaption
import ru.lextop.steamcalculator.binding.toVisibleOrGone

class DropdownHintArrayAdapter(context: Context)
    : ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item) {
    private val ui = AnkoContext.createReusable(context)
    var isHintVisible: Boolean = true

    var items: List<Pair<CharSequence, CharSequence>> = listOf()
        set(value) {
            field = value
            clear()
            addAll(value.map { it.first })
            notifyDataSetChanged()
        }

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?:
                ui.linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    ankoView(
                            { super.getDropDownView(position, convertView, parent) }, 0,
                            { layoutParams = LinearLayout.LayoutParams(wrapContent, layoutParams.height) })
                    textCaption {
                        verticalPadding = context.dip(8)
                        endPadding = context.dip(8)
                    }
                }
        view as LinearLayout
        val hint = view.getChildAt(1) as TextView
        if (isHintVisible) {
            hint.text = items[position].second
        }
        hint.visibility = isHintVisible.toVisibleOrGone()
        view.baselineAlignedChildIndex = 0
        return view
    }
}