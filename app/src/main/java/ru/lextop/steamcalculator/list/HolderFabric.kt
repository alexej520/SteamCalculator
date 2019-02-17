package ru.lextop.steamcalculator.list

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import ru.lextop.steamcalculator.binding.inflate
import kotlin.reflect.KClass

abstract class HolderFabric : Filter {
    abstract val layoutRes: Int
    open val viewType: Int get() = layoutRes
    fun create(adapter: Adapter<*>, parent: ViewGroup): Holder<*> {
        val view = if (layoutRes == 0) {
            FrameLayout(parent.context)
        } else {
            parent.inflate(layoutRes)
        }
        val holder = create(view)
        holder.init(adapter, viewType)
        return holder
    }

    protected abstract fun create(view: View): Holder<*>
}

@Suppress("UNCHECKED_CAST")
fun List<Any>.toHolderFabrics(): List<HolderFabric> {
    return map {
        when (it) {
            is HolderFabric -> it
            is KClass<*> -> AnnotationHolderFabric(it as KClass<out Holder<*>>)
            else -> throw IllegalArgumentException()
        }
    }
}