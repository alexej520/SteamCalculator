package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

class SimpleBindingAdapter<VM : Any>(private val bindingLo: LifecycleOwner, private val bc: Binding.Component<VM, ViewGroup>)
    : RecyclerView.Adapter<BindingHolder<VM>>() {
    var viewModels: List<VM> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<VM> {
        return BindingHolder.create(parent, bindingLo, bc)
    }

    override fun onBindViewHolder(holder: BindingHolder<VM>, position: Int) {
        holder.bind(viewModels[position])
    }

    override fun onViewRecycled(holder: BindingHolder<VM>) {
        holder.unbind()
    }

    override fun getItemCount() = viewModels.size
}