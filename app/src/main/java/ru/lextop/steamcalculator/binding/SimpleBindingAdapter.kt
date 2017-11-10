package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

open class SimpleBindingAdapter<VM : Any, LO : LifecycleOwner>(private val lo: LO, private val bc: Binding.Component<VM, LO>)
    : RecyclerView.Adapter<BindingHolder<VM, LO>>() {
    var viewModels: List<VM> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<VM, LO> {
        return BindingHolder.create(parent, lo, bc)
    }

    override fun onBindViewHolder(holder: BindingHolder<VM, LO>, position: Int) {
        holder.bind(viewModels[position])
    }

    override fun onViewRecycled(holder: BindingHolder<VM, LO>) {
        holder.unbind()
    }

    override fun getItemCount() = viewModels.size
}