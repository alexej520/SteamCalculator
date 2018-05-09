package ru.lextop.steamcalculator.ui

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class BlockRequestChildRectangleOnScreenLinearLayoutManager : LinearLayoutManager {
    constructor(
        context: Context
    ) : super(
        context
    )

    constructor(
        context: Context,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun requestChildRectangleOnScreen(
        parent: RecyclerView?,
        child: View?,
        rect: Rect?,
        immediate: Boolean
    ): Boolean {
        return false
    }

    override fun requestChildRectangleOnScreen(
        parent: RecyclerView?,
        child: View?,
        rect: Rect?,
        immediate: Boolean,
        focusedChildVisible: Boolean
    ): Boolean {
        return false
    }
}