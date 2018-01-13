package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.view.View
import org.jetbrains.anko.AnkoContext
import java.util.*

open class Binding<VM : Any> {
    val nested: MutableMap<Any, Binding<*>> = mutableMapOf()
    private var _view: View? = null
    fun setView(view: View) {
        _view = view
        Companion.addBinding(this)
    }

    var viewModel: VM? = null
        private set

    var viewModelLO: LifecycleOwner? = null
        private set

    val view
        get() = _view!!

    private val liveDataObservers = mutableListOf<Pair<LiveData<Any?>, Observer<Any?>>>()

    private val bindings = mutableListOf<(VM?) -> Unit>()
    private val liveBindings = mutableListOf<(VM?, LifecycleOwner?) -> Unit>()

    fun setViewModel(viewModelLO: LifecycleOwner?, viewModel: VM?) {
        this.viewModel = viewModel
        this.viewModelLO = viewModelLO
        liveDataObservers.forEach { (liveData, observer) ->
            liveData.removeObserver(observer)
        }
        liveDataObservers.clear()
        liveBindings.forEach { it(viewModel, viewModelLO) }
        bindings.forEach { it(viewModel) }
        nested.forEach { (key, binding) ->
            @Suppress("UNCHECKED_CAST")
            (binding as Binding<Any>).setViewModel(viewModelLO, (viewModel as? BindingViewModel)?.nested?.get(key))
        }
    }

    fun callback(receive: VM.() -> Unit) {
        viewModel?.receive()
    }

    fun <T> bind(receive: (T?) -> Unit, send: VM.() -> T) {
        bindings += { vm: VM? ->
            receive(vm?.send())
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> bindLive(receive: (T?) -> Unit, send: VM.() -> LiveData<T>) {
        liveBindings += { vm: VM?, lo: LifecycleOwner? ->
            if (lo == null) {
                throw IllegalStateException("cannot bindLive if lifecycleOwner == null")
            } else {
                if (vm != null) {
                    val liveData = vm.send() as LiveData<Any?>
                    val observer = Observer<Any?> {
                        receive(it as T?)
                    }
                    liveData.observe(lo, observer)
                    liveDataObservers.add(liveData to observer)
                }
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

    abstract class  Component<VM : Any, T> {
        abstract fun createBinding(ui: AnkoContext<T>): Binding<VM>
    }

    abstract class SimpleComponent<VM : Any, T>: Component<VM, T>() {
        abstract protected fun Binding<VM>.createView(ui: AnkoContext<T>): View
        override final fun createBinding(ui: AnkoContext<T>): Binding<VM> {
            val binding = Binding<VM>()
            binding.setView(binding.createView(ui))
            return binding
        }
    }
}

interface BindingViewModel {
    val nested: MutableMap<Any, Any>
}
