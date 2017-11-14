package ru.lextop.steamcalculator.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.binding.toVisibleOrGone

class CharSequensePairArrayAdapter(context: Context,
                                   var isFirstVisible: Boolean = true,
                                   var isSecondVisible: Boolean = true,
                                   var isFirstInDropdownVisible: Boolean = true,
                                   var isSecondInDropdownVisible: Boolean = true)
    : ArrayAdapter<Pair<CharSequence, CharSequence>>(context, R.layout.spinner_item_pair) {
    private val inflater = LayoutInflater.from(context)
    private val resource = R.layout.spinner_item_pair


    var items: List<Pair<CharSequence, CharSequence>> = listOf()
        set(value) {
            field = value
            clear()
            addAll(value)
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Pair<CharSequence, CharSequence> = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(resource, parent, false)
        view as ViewGroup
        val (text1, text2) = getItem(position)!!
        val view1 = view.getChildAt(0) as TextView
        val view2 = view.getChildAt(1) as TextView
        view1.text = text1
        view2.text = text2
        view1.visibility = isFirstVisible.toVisibleOrGone()
        view2.visibility = isSecondVisible.toVisibleOrGone()
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(resource, parent, false)
        view as ViewGroup
        val (text1, text2) = getItem(position)!!
        val view1 = view.getChildAt(0) as TextView
        val view2 = view.getChildAt(1) as TextView
        view1.text = text1
        view2.text = text2
        view1.visibility = isFirstInDropdownVisible.toVisibleOrGone()
        view2.visibility = isSecondInDropdownVisible.toVisibleOrGone()
        return view
    }
}