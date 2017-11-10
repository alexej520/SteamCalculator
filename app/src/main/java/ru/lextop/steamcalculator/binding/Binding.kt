package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.view.View
import org.jetbrains.anko.AnkoContext
import java.util.*

class Binding<VM : Any> private constructor() {
    private var _view: View? = null
    private fun setView(view: View) {
        _view = view
        Companion.addBinding(this)
    }

    val view
        get() = _view!!
    private val liveViewModel = MutableLiveData<Pair<LifecycleOwner?, VM?>>()
    private val liveBindings = mutableListOf<Pair<LiveData<Any?>, Observer<Any?>>>()
    fun setViewModel(viewModelLO: LifecycleOwner?, viewModel: VM?) {
        liveBindings.forEach { (liveData, observer) ->
            liveData.removeObserver(observer)
        }
        liveViewModel.value = viewModelLO to viewModel
    }

    fun LifecycleOwner.notify(receive: VM.() -> Unit) {
        val pair = liveViewModel.value
        if (pair != null) {
            val (lo, vm) = pair
            if (vm == null) {
            } else {
                vm.receive()
            }
        }
    }

    fun <T> LifecycleOwner.bind(receive: (T?) -> Unit, send: VM.() -> T) {
        liveViewModel.observe(this) {
            val (lo, vm) = it!!
            if (vm == null) {
                receive(null)
            } else {
                receive(vm.send())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> LifecycleOwner.bindLive(receive: (T?) -> Unit, send: VM.() -> LiveData<T>) {
        liveViewModel.observe(this) {
            val (lo, vm) = it!!
            if (vm == null || lo == null) {
                receive(null)
            } else {
                val liveData = vm.send() as LiveData<Any?>
                val observer = Observer<Any?> {
                    if (it != null) receive(it as T)
                }
                liveData.observe(lo, observer)
                liveBindings.add(liveData to observer)
            }
        }
    }

    companion object {
        private val weakBindingMap: WeakHashMap<Binding<*>, Any?> = WeakHashMap()
        private val weakBindingSet = weakBindingMap.keys
        private fun addBinding(binding: Binding<*>) {
            weakBindingMap[binding] = null
        }

        @Suppress("UNCHECKED_CAST")
        fun <VM : Any> getForView(view: View) = weakBindingSet.first { view == it.view } as Binding<VM>
    }

    abstract class Component<VM : Any, in LO : LifecycleOwner> {
        abstract fun Binding<VM>.createView(ui: AnkoContext<LO>): View
        fun createBinding(ui: AnkoContext<LO>): Binding<VM> {
            val binding = Binding<VM>()
            binding.setView(binding.createView(ui))
            return binding
        }
    }
}