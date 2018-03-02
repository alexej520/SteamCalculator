package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.jetbrains.anko.layoutInflater
import ru.lextop.steamcalculator.binding.BindingHolder
import ru.lextop.steamcalculator.binding.DataBoundViewHolder
import ru.lextop.steamcalculator.databinding.ItemQuantityBinding
import ru.lextop.steamcalculator.vm.QuantityValueViewModel
import ru.lextop.steamcalculator.vm.RefractiveIndexViewModel

class SteamBindingAdapter(private val bindingLo: LifecycleOwner)
    : RecyclerView.Adapter<DataBoundViewHolder<*>>() {
    companion object {
        const val REFRACTIVE_INDEX_BINDING_ID = 1
    }

    private val bc = QuantityUI()
    var viewModels: List<QuantityValueViewModel> = listOf()
    var refractiveIndexViewModel: RefractiveIndexViewModel? = null
    val ribc = RefractiveIndexUI()


    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
            (holder as BindingHolder<RefractiveIndexViewModel>).bind(refractiveIndexViewModel!!)
        } else {
            val binding = (holder as DataBoundViewHolder<ItemQuantityBinding>).binding
            binding.setLifecycleOwner(bindingLo)
            val item = viewModels[position]
            binding.vm = item
            (binding.quantityUnits.adapter as UnitArrayAdapter).items = item.units
        }
    }

    override fun getItemCount() = viewModels.size + if (refractiveIndexViewModel != null) 1 else 0
    override fun getItemViewType(position: Int): Int =
            if (position < viewModels.size)
                super.getItemViewType(position)
            else
                REFRACTIVE_INDEX_BINDING_ID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> =
            if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
                throw Exception()
                //BindingHolder.create(parent, bindingLo, ribc)
            } else {
                val binding = ItemQuantityBinding.inflate(parent.context.layoutInflater, parent, false)
                binding.quantityUnits.adapter = UnitArrayAdapter(parent.context)
                DataBoundViewHolder(binding)
                //BindingHolder.create(parent, bindingLo, bc)
            }
}
