package ru.lextop.steamcalculator.ui

import android.content.Context
import android.widget.ArrayAdapter

class SimpleArrayAdapter(context: Context) :
        ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_dropdown_item) {
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
}