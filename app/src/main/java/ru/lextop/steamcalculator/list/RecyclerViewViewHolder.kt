package ru.lextop.steamcalculator.list

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewViewHolder(
    val holder: Holder<*>
) : RecyclerView.ViewHolder(
    holder.containerView
) {
    init {
        setIsRecyclable(holder.isRecyclable)
    }
}