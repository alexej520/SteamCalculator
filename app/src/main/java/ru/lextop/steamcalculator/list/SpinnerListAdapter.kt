package ru.lextop.steamcalculator.list

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ru.lextop.steamcalculator.R

open class SpinnerListAdapter<T>(
    holderFabrics: List<Pair<Any, Any>>
) : BaseAdapter(), Adapter<T> {
    private val viewHolderFabrics: List<HolderFabric>
    private val dropdownHolderFabrics: List<HolderFabric>

    init {
        val (view, dropdown) = holderFabrics.unzip()
        viewHolderFabrics = view.toHolderFabrics()
        dropdownHolderFabrics = dropdown.toHolderFabrics()
    }

    private var items: List<T>? = null

    fun submitList(items: List<T>?) {
        if (this.items == items) {
            return
        }
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): T {
        return items!![position]
    }

    override fun getItemId(position: Int): Long {
        return -1L
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return viewHolderFabrics.first { it.filter(item) }.viewType
    }

    override fun getCount(): Int {
        return items?.size ?: 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(dropdownHolderFabrics, position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(viewHolderFabrics, position, convertView, parent)
    }

    private fun getView(
        holderFabrics: List<HolderFabric>,
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val viewType = getItemViewType(position)
        @Suppress("UNCHECKED_CAST")
        val oldHolder = convertView?.getTag(R.id.tag_holder) as? Holder<T>
        val newHolder =
            if (oldHolder == null || !oldHolder.isRecyclable || oldHolder.viewType != viewType) {
                holderFabrics.first { it.viewType == viewType }.create(this, parent)
            } else {
                oldHolder
            }
        newHolder.containerView.setTag(R.id.tag_holder, newHolder)
        newHolder.position = position
        newHolder.bind(null)
        return newHolder.containerView
    }
}