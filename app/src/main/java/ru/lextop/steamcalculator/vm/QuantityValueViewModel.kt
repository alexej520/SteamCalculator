package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.*
import android.content.Context
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.binding.getSpanned
import quantityvalue.*
import ru.lextop.steamcalculator.model.nameId
import ru.lextop.steamcalculator.model.symbolId
import ru.lextop.steamcalculator.model.unitList

class QuantityValueViewModel(
        quantityValueLive: LiveData<QuantityValue>,
        unitLive: LiveData<UnitPh>,
        context: Context,
        val isPropNameVisibleLive: LiveData<Boolean>,
        val onUnitSelect: (UnitPh) -> Unit)
    : ViewModel() {
    private val quantity = quantityValueLive.value!!.quantity
    val quantityName = context.getSpanned(quantity.nameId)
    val quantitySymbol = context.getSpanned(quantity.symbolId)
    val valueLive: LiveData<CharSequence> = MutableLiveData()

    fun updateValue() { value = value }

    private var value = Double.NaN
        set(value) {
            field = value
            (valueLive as MutableLiveData).value = CustomFormat.format(value)
        }
    private val _units = quantityValueLive.value!!.quantity.dimension.unitList
    val units = _units.map {
        context.getSpanned(it.symbolId)
    }
    val unitSelectionLive: LiveData<Int> = MutableLiveData()

    init {
        quantityValueLive.observeForever {
            value = it!![unitLive.value!!].value
        }
        unitLive.observeForever { unit ->
            unitSelectionLive as MutableLiveData
            unitSelectionLive.value = _units.indexOf(unit!!)
            value = quantityValueLive.value!![unit].value
        }
    }

    fun selectUnit(id: Int) {
        onUnitSelect(_units[id])
    }
}