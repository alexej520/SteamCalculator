package ru.lextop.steamcalculator.list

import android.view.View
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

class AnnotationHolderFabric(holderKClass: KClass<out Holder<*>>) : HolderFabric() {
    override val layoutRes: Int
    override val viewType: Int
    private val constructor: Constructor<out Holder<*>>
    private val filter: Filter

    init {
        val fabricProvider = holderKClass.java.annotations
            .first { it is ProvideHolderFabric } as ProvideHolderFabric
        layoutRes = fabricProvider.layoutRes
        viewType = if (fabricProvider.viewType == -1) layoutRes else fabricProvider.viewType
        constructor = holderKClass.java.getConstructor(View::class.java)
        constructor.isAccessible = true
        val filterConstructor = fabricProvider.filter.java.getConstructor()
        filterConstructor.isAccessible = true
        filter = filterConstructor.newInstance()
    }

    override fun create(view: View): Holder<*> {
        return constructor.newInstance(view)
    }

    override fun filter(item: Any?): Boolean {
        return filter.filter(item)
    }
}