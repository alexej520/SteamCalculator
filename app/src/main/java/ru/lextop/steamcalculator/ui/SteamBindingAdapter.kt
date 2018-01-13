package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import ru.lextop.steamcalculator.binding.BindingHolder
import ru.lextop.steamcalculator.vm.QuantityValueViewModel
import ru.lextop.steamcalculator.vm.RefractiveIndexViewModel

class SteamBindingAdapter(private val bindingLo: LifecycleOwner)
    : RecyclerView.Adapter<BindingHolder<*>>() {
    companion object {
        const val REFRACTIVE_INDEX_BINDING_ID = 1
    }

    private val bc = QuantityUI()
    var viewModels: List<QuantityValueViewModel> = listOf()
    var refractiveIndexViewModel: RefractiveIndexViewModel? = null
    val ribc = RefractiveIndexUI()


    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BindingHolder<*>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
            (holder as BindingHolder<RefractiveIndexViewModel>).bind(refractiveIndexViewModel!!)
        } else {
            (holder as BindingHolder<QuantityValueViewModel>).bind(viewModels[position])
        }
        Unit
    }

    override fun onViewRecycled(holder: BindingHolder<*>) {
        holder.unbind()
    }

    override fun getItemCount() = viewModels.size + if (refractiveIndexViewModel != null) 1 else 0
    override fun getItemViewType(position: Int): Int =
            if (position < viewModels.size)
                super.getItemViewType(position)
            else
                REFRACTIVE_INDEX_BINDING_ID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<*> =
            if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
                BindingHolder.create(parent, bindingLo, ribc)
            } else {
                BindingHolder.create(parent, bindingLo, bc)
            }
}
