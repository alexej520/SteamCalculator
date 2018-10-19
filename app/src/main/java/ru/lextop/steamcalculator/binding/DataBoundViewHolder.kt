package ru.lextop.steamcalculator.binding

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class DataBoundViewHolder<DB : ViewDataBinding>(
    val binding: DB
) : RecyclerView.ViewHolder(binding.root)
