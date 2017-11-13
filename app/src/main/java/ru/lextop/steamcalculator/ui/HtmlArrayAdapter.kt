package ru.lextop.steamcalculator.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.binding.spannedFromHtml


class HtmlArrayAdapter<T>(context: Context, objects: List<T>)
    : ArrayAdapter<T>(context, R.layout.html_spinner_item, objects) {
    private val inflater = LayoutInflater.from(context)
    private val resource = R.layout.html_spinner_item

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(resource, parent, false)
        val text: TextView
        text = view as TextView

        val item = getItem(position)!!
        text.text = spannedFromHtml(item.toString())
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}