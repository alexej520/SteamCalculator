package ru.lextop.steamcalculator.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.lextop.steamcalculator.R

class CharSequenceArrayAdapter(context: Context)
    : ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item) {
    var items: List<CharSequence> = listOf()
        set(value) {
            field = value
            clear()
            addAll(value)
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): CharSequence = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: TextView(context, null, 0, R.style.SpinnerItem)
        (view as TextView).text = getItem(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: TextView(context, null, 0, R.style.DropDownItem_Spinner)
        (view as TextView).text = getItem(position)
        return view
    }
}