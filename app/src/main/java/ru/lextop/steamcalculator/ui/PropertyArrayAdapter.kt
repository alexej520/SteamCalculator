package ru.lextop.steamcalculator.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.jetbrains.anko.layoutInflater
import ru.lextop.steamcalculator.steam.quantity.Property

class PropertyArrayAdapter(context: Context)
    : ArrayAdapter<Property>(context, android.R.layout.simple_spinner_item) {
    var properties: List<Property>? = null
        set(props) {
            clear()
            addAll(props)
            notifyDataSetChanged()
            field = props
        }

    override fun getItem(position: Int): Property? = properties?.get(position)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getDropDownView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: context.layoutInflater.inflate(android.R.layout.simple_spinner_item, null)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = properties?.get(position)?.name
        return view
    }
}