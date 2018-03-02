package ru.lextop.steamcalculator.binding

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

class DataBoundViewHolder<DB : ViewDataBinding>(
    val binding: DB
) : RecyclerView.ViewHolder(binding.root)
