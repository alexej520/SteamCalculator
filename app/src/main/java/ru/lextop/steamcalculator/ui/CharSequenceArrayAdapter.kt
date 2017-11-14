package ru.lextop.steamcalculator.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import ru.lextop.steamcalculator.R

class CharSequenceArrayAdapter(context: Context)
    : ArrayAdapter<CharSequence>(context, R.layout.spinner_item) {
    private val inflater = LayoutInflater.from(context)
    private val resource = R.layout.spinner_item

    var items: List<CharSequence> = listOf()
        set(value) {
            field = value
            clear()
            addAll(value)
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): CharSequence = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(resource, parent, false)
        (view as TextView).text = getItem(position)!!
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}