package ru.lextop.steamcalculator.vm

import androidx.lifecycle.LiveData

abstract class SelectQuantityViewModel {
    abstract val quantityNameToSymbolListLive: LiveData<List<Pair<CharSequence, CharSequence>>>
    abstract val nameLive: LiveData<CharSequence>
    abstract val nameVisibleLive: LiveData<Boolean>
    abstract val quantitySelectionLive: LiveData<Int>
    abstract fun selectQuantity(position: Int)
    abstract val valueLive: LiveData<CharSequence>
    abstract fun inputValue(value: CharSequence)
    abstract val focusLive: LiveData<Boolean>
    abstract val unitsLive: LiveData<List<CharSequence>>
    abstract val unitSelectionLive: LiveData<Int>
    abstract fun selectUnit(position: Int)
}
