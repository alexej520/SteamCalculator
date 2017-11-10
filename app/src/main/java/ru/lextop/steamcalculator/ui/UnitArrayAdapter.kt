package ru.lextop.steamcalculator.ui

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.jetbrains.anko.layoutInflater
import ru.lextop.steamcalculator.resBaseUnitMap
import ru.lextop.steamcalculator.steam.quantity.DerivativeUnit


class UnitArrayAdapter(context: Context)
    : ArrayAdapter<Any>(context, android.R.layout.simple_spinner_item) {
    private val localizedUnitNames = mutableListOf<CharSequence>()
    var units: List<DerivativeUnit>? = null
        set(units) {
            localizedUnitNames.clear()
            clear()
            addAll(units)
            notifyDataSetChanged()
            field = units
            if (units == null) return
            val resId = resBaseUnitMap[units.first().baseUnit]!!
            val localizedUnitNamesMap = mutableMapOf<String, String>()
            var key: String? = null
            for (s in context.getString(resId).split('`')) {
                key = if (key == null) {
                    s
                } else {
                    localizedUnitNamesMap[key.trimStart()] = s
                    null
                }
            }
            units.forEach {
                val name = it.name
                val localizedNameHtml = localizedUnitNamesMap[name]
                val localizedName = if (localizedNameHtml == null) {
                    null
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(localizedNameHtml, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        @Suppress("DEPRECATION")
                        Html.fromHtml(localizedNameHtml)
                    }
                }
                localizedUnitNames += localizedName ?: name
            }
        }


    override fun getItem(position: Int): CharSequence {
        return localizedUnitNames[position]
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getDropDownView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: context.layoutInflater.inflate(android.R.layout.simple_spinner_item, null)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = localizedUnitNames[position]
        return view
    }
}