package ru.lextop.steamcalculator.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.linearLayout
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.binding.toVisibleOrGone

class CharSequensePairArrayAdapter(context: Context,
                                   var isFirstVisible: Boolean = true,
                                   var isSecondVisible: Boolean = true,
                                   var isFirstInDropdownVisible: Boolean = true,
                                   var isSecondInDropdownVisible: Boolean = true)
    : ArrayAdapter<Pair<CharSequence, CharSequence>>(context, android.R.layout.simple_spinner_item) {
    private val ui = AnkoContext.createReusable(context)


    var items: List<Pair<CharSequence, CharSequence>> = listOf()
        set(value) {
            field = value
            clear()
            addAll(value)
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Pair<CharSequence, CharSequence> = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: {
            val ll = LinearLayout(context)
            ll.addView(TextView(context, null, 0, R.style.SpinnerItem))
            ll.addView(TextView(context, null, 0, R.style.TextCaption))
            ll
        }()
        view as LinearLayout
        val (text1, text2) = getItem(position)
        val view1 = view.getChildAt(0) as TextView
        val view2 = view.getChildAt(1) as TextView
        view1.text = text1
        view2.text = text2
        view1.visibility = isFirstVisible.toVisibleOrGone()
        view2.visibility = isSecondVisible.toVisibleOrGone()
        view.baselineAlignedChildIndex = when {
            isFirstVisible -> 0
            isSecondVisible -> 1
            else -> 0
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: {
            val ll = LinearLayout(context)
            ll.addView(TextView(context, null, 0, R.style.DropDownItem_Spinner))
            ll.addView(TextView(context, null, 0, R.style.TextCaption))
            ll
        }()
        view as LinearLayout
        val (text1, text2) = getItem(position)
        val view1 = view.getChildAt(0) as TextView
        val view2 = view.getChildAt(1) as TextView
        view1.text = text1
        view2.text = text2
        view1.visibility = isFirstInDropdownVisible.toVisibleOrGone()
        view2.visibility = isSecondInDropdownVisible.toVisibleOrGone()
        return view
    }
}