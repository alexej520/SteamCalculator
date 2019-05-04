package ru.lextop.steamcalculator.list

import android.view.View
import kotlinx.android.extensions.LayoutContainer

abstract class Holder<T>(
    override val containerView: View
): LayoutContainer {
    private lateinit var _adapter: Adapter<*>
    private var _viewType: Int = 0
    internal val viewType: Int get() = _viewType
    internal fun init(adapter: Adapter<*>, viewType: Int) {
        _adapter = adapter
        _viewType = viewType
    }

    @Suppress("UNCHECKED_CAST")
    protected val adapter: Adapter<T>
        get() = _adapter as Adapter<T>
    var position: Int = -1
        internal set
    @Suppress("UNCHECKED_CAST")
    val item: T
        get() = adapter.getItem(position)
    var isRecyclable: Boolean = true

    abstract fun bind(payload: Any?)
}