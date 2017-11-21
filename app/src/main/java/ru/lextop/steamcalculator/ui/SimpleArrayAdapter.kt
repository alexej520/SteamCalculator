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

class SimpleArrayAdapter(context: Context) :
        ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item) {
    private val ui = AnkoContext.createReusable(context)
    var items: List<CharSequence> = listOf()
        set(value) {
            field = value
            clear()
            addAll(value)
            notifyDataSetChanged()
        }

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?:
                ui.linearLayout {
                    var startPad = 0
                    var endPad = 0
                    ankoView({ super.getView(position, convertView, parent) as TextView }, 0) {
                        startPad = startPadding
                        endPad = endPadding
                        horizontalPadding = 0
                        verticalPadding = (textSize / 4).toInt()
                        setEms(4)
                    }.lparams{
                        marginStart = startPad
                        marginEnd = endPad
                    }
                    baselineAlignedChildIndex = 0
                }
        val textView = (view as LinearLayout).getChildAt(0) as TextView
        textView.text = getItem(position)
        return view
    }
}