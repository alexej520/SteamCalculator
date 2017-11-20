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
import ru.lextop.steamcalculator.binding.startPadding
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?:
                ui.linearLayout {
                    var padding = 0
                    ankoView({ super.getView(position, convertView, parent) as TextView }, 0) {
                        setEms(1)
                        padding = startPadding
                        horizontalPadding = 0
                    }.lparams{
                        horizontalMargin = padding
                    }
                    baselineAlignedChildIndex = 0
                }
        val textView = (view as LinearLayout).getChildAt(0) as TextView
        textView.text = getItem(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?:
                ui.linearLayout {
                    var padding = 0
                    ankoView({ super.getView(position, convertView, parent) as TextView }, 0) {
                        setEms(1)
                        padding = startPadding
                        horizontalPadding = 0
                    }.lparams{
                        horizontalMargin = padding
                    }
                    textCaption {
                        verticalPadding = context.dip(8)
                        endPadding = context.dip(8)
                    }
                    baselineAlignedChildIndex = 0
                }

        val (item, hint) = items[position]
        view as LinearLayout
        val itemView = view.getChildAt(0).findViewById<TextView>(android.R.id.text1)
        itemView.text = item
        val hintView = view.getChildAt(1) as TextView
        if (isHintVisible) {
            hintView.text = hint
        }
        hintView.visibility = isHintVisible.toVisibleOrGone()
        view.baselineAlignedChildIndex = 0
        return view
    }
}