package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext

class BindingHolder<VM : Any> private constructor(view: View, bindingLo: LifecycleOwner)
    : RecyclerView.ViewHolder(view), LifecycleOwner {
    init {
        bindingLo.lifecycle.addObserver(GenericLifecycleObserver { _, event ->
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
        fun <VM : Any> create(parent: ViewGroup, bindingLO: LifecycleOwner, bc: Binding.Component<VM, ViewGroup>)
                : BindingHolder<VM> {
            val context = AnkoContext.create(parent.context, parent)
            val binding = bc.createBinding(bindingLO, context)
            val holder = BindingHolder<VM>(binding.view, bindingLO)
            holder.setBinding(binding)
            return holder
        }
    }
}
