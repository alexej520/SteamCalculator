package ru.lextop.steamcalculator.ui

import android.content.Context
import android.util.AttributeSet
import ru.lextop.steamcalculator.R

class Toolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.toolbarStyle
) : androidx.appcompat.widget.Toolbar(
    context,
    attrs,
    defStyleAttr
) {
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, defStyleAttr, 0)
        val menuRes = a.getResourceId(R.styleable.Toolbar_menu, 0)
        a.recycle()

        if (menuRes != 0) {
            inflateMenu(menuRes)
        }
    }
}