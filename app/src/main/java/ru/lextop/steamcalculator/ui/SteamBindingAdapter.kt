package ru.lextop.steamcalculator.ui

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.verticalLayout
import ru.lextop.steamcalculator.binding.Binding
import ru.lextop.steamcalculator.binding.BindingHolder
import ru.lextop.steamcalculator.binding.BindingViewModel
import ru.lextop.steamcalculator.vm.QuantityValueViewModel

class SimpleSteamAdapter(private val bindingLo: LifecycleOwner)
    : RecyclerView.Adapter<BindingHolder<*>>() {
    companion object {
        const val REFRACTIVE_INDEX_BINDING_ID = 1
    }

    private val bc = QuantityUI()
    var viewModels: List<QuantityValueViewModel> = listOf()
    val refractiveIndexBinding = RefractiveIndexViewModel()
    val ribc = RefractiveIndexUI()


    override fun onBindViewHolder(holder: BindingHolder<*>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == REFRACTIVE_INDEX_BINDING_ID) {
            (holder as BindingHolder<RefractiveIndexViewModel>).bind(refractiveIndexBinding)
        } else {
            (holder as BindingHolder<QuantityValueViewModel>).bind(viewModels[position])
        }
        Unit
    }

    override fun onViewRecycled(holder: BindingHolder<*>) {
        holder.unbind()
    }

    override fun getItemCount() = viewModels.size + 1
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

class RefractiveIndexUI : Binding.Component<RefractiveIndexViewModel, ViewGroup>() {
    val riUI = QuantityUI()
    override fun createBinding(ui: AnkoContext<ViewGroup>): Binding<RefractiveIndexViewModel> {
        val binding = Binding<RefractiveIndexViewModel>()
        val refractiveBinding = riUI.createBinding(ui)
        binding.nested.put(RefractiveIndexViewModel::refractiveIndex, refractiveBinding)
        with(ui){
            verticalLayout {
                ankoView({ refractiveBinding.view }, 0, {})
                binding.setView(this)
            }
        }
        return binding
    }
}

class RefractiveIndexViewModel: BindingViewModel{
    override val nested: MutableMap<Any, Any> = mutableMapOf()
    lateinit var refractiveIndex: QuantityValueViewModel
    init {
        nested[RefractiveIndexViewModel::refractiveIndex] = refractiveIndex
    }
}
