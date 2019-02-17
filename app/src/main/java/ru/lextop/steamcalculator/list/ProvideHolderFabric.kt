package ru.lextop.steamcalculator.list

import androidx.annotation.LayoutRes
import kotlin.reflect.KClass

annotation class ProvideHolderFabric(
    @LayoutRes
    val layoutRes: Int = 0,
    val viewType: Int = -1,
    val filter: KClass<out Filter> = DefaultFilter::class
)