package ru.lextop.steamcalculator.ui

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import ru.lextop.steamcalculator.R


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

    private fun spannedFromHtml(string: String) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(string)
            }
}