package ru.lextop.steamcalculator.ui

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import ru.lextop.steamcalculator.binding.DataBoundViewHolder
import ru.lextop.steamcalculator.binding.inflate
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

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
            val binding = (holder as DataBoundViewHolder<ItemRefractiveindexBinding>).binding
            binding.lifecycleOwner = bindingLo
            binding.refractiveIndex.lifecycleOwner = bindingLo
            binding.vm = refractiveIndexViewModel

            binding.executePendingBindings()

            binding.wavelengthUnitAdapter!!.submitList(
                refractiveIndexViewModel?.wavelengthUnits
            )
            binding.refractiveIndex.unitAdapter!!.submitList(
                refractiveIndexViewModel?.refractiveIndexQuantityValueViewModel?.units
            )

        } else {
            val binding = (holder as DataBoundViewHolder<ItemQuantityBinding>).binding
            binding.lifecycleOwner = bindingLo
            val item = viewModels[position]
            binding.vm = item

            binding.executePendingBindings()

            binding.unitAdapter!!.submitList(item.units)
        }
    }

    override fun getItemCount() = viewModels.size + if (refractiveIndexViewModel != null) 1 else 0
    override fun getItemViewType(position: Int): Int =
        if (position < viewModels.size)
            super.getItemViewType(position)
        else
            REFRACTIVE_INDEX_BINDING_ID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        return if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
            val binding = parent.inflate(ItemRefractiveindexBinding::inflate)
            binding.wavelengthUnitAdapter = UnitArrayAdapter()
            binding.refractiveIndex.unitAdapter = UnitArrayAdapter()
            DataBoundViewHolder(binding)
        } else {
            val binding = parent.inflate(ItemQuantityBinding::inflate)
            binding.unitAdapter = UnitArrayAdapter()
            DataBoundViewHolder(binding)
        }
    }
}
