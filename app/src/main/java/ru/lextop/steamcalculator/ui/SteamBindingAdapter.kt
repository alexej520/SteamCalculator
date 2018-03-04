package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jetbrains.anko.layoutInflater
import ru.lextop.steamcalculator.binding.DataBoundViewHolder
import ru.lextop.steamcalculator.databinding.ItemQuantityBinding
import ru.lextop.steamcalculator.databinding.ItemRefractiveindexBinding
import ru.lextop.steamcalculator.vm.QuantityValueViewModel
import ru.lextop.steamcalculator.vm.RefractiveIndexViewModel

class SteamBindingAdapter(private val bindingLo: LifecycleOwner) :
    RecyclerView.Adapter<DataBoundViewHolder<*>>() {
    companion object {
        const val REFRACTIVE_INDEX_BINDING_ID = 1
    }

    var viewModels: List<QuantityValueViewModel> = listOf()
    var refractiveIndexViewModel: RefractiveIndexViewModel? = null
    var layoutInflater: LayoutInflater? = null

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
            val binding = (holder as DataBoundViewHolder<ItemRefractiveindexBinding>).binding
            binding.setLifecycleOwner(bindingLo)
            binding.refractiveIndex.setLifecycleOwner(bindingLo)
            binding.vm = refractiveIndexViewModel

            binding.executePendingBindings()

            binding.wavelengthUnitAdapter!!.items =
                    refractiveIndexViewModel?.wavelengthUnits ?: emptyList()
            binding.refractiveIndex.unitAdapter!!.items =
                    refractiveIndexViewModel?.refractiveIndexQuantityValueViewModel?.units ?:
                    emptyList()
        } else {
            val binding = (holder as DataBoundViewHolder<ItemQuantityBinding>).binding
            binding.setLifecycleOwner(bindingLo)
            val item = viewModels[position]
            binding.vm = item

            binding.executePendingBindings()

            binding.unitAdapter!!.items = item.units
        }
    }

    override fun getItemCount() = viewModels.size + if (refractiveIndexViewModel != null) 1 else 0
    override fun getItemViewType(position: Int): Int =
        if (position < viewModels.size)
            super.getItemViewType(position)
        else
            REFRACTIVE_INDEX_BINDING_ID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        if (layoutInflater == null) {
            layoutInflater = parent.context.layoutInflater
        }
        return if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
            val binding =
                ItemRefractiveindexBinding.inflate(layoutInflater!!, parent, false)
            binding.wavelengthUnitAdapter = UnitArrayAdapter(parent.context)
            binding.refractiveIndex.unitAdapter = UnitArrayAdapter(parent.context)
            DataBoundViewHolder(binding)
        } else {
            val binding = ItemQuantityBinding.inflate(layoutInflater!!, parent, false)
            binding.unitAdapter = UnitArrayAdapter(parent.context)
            DataBoundViewHolder(binding)
        }
    }
}
