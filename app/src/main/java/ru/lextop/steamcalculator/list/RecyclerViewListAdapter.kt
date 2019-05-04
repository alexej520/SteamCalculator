package ru.lextop.steamcalculator.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class RecyclerViewListAdapter<T>(
    holderFabrics: List<Any>,
    itemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, RecyclerViewViewHolder>(
    itemCallback
), Adapter<T> {
    private val holderFabrics = holderFabrics.toHolderFabrics()
    override fun getItem(position: Int): T {
        return super.getItem(position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return holderFabrics.first { it.filter(item) }.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val holder = holderFabrics.first { it.viewType == viewType }.create(this, parent)
        return RecyclerViewViewHolder(holder)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
    }

    override fun onBindViewHolder(
        holder: RecyclerViewViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.holder.position = position
        holder.holder.bind(payloads.firstOrNull())
    }
}