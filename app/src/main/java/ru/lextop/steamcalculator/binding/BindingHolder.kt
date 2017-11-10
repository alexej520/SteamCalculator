package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext

class BindingHolder<VM : Any, LO : LifecycleOwner> private constructor(view: View, context: AnkoContext<LO>)
    : RecyclerView.ViewHolder(view), LifecycleOwner {
    init {
        context.owner.lifecycle.addObserver(GenericLifecycleObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY) {
                _lifecycle.handleLifecycleEvent(event)
            }
        })
    }

    private val _lifecycle = LifecycleRegistry(this)
    private lateinit var _binding: Binding<VM>
    override fun getLifecycle() = _lifecycle

    private fun setBinding(binding: Binding<VM>) {
        _binding = binding
    }

    fun bind(vm: VM) {
        _lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        _binding.setViewModel(this, vm)
    }

    fun unbind() {
        _lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    companion object {
        fun <VM : Any, LO : LifecycleOwner> create(parent: ViewGroup, parentLO: LO, bc: Binding.Component<VM, LO>)
                : BindingHolder<VM, LO> {
            val context = AnkoContext.create(parent.context, parentLO)
            val binding = bc.createBinding(context)
            val holder = BindingHolder<VM, LO>(binding.view, context)
            holder.setBinding(binding)
            return holder
        }
    }
}
