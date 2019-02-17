package ru.lextop.steamcalculator.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.model.QuantityValueWrapper
import ru.lextop.steamcalculator.model.UnitConverterWrapper

class QuantityValueViewModel(
        quantityValueLive: LiveData<QuantityValueWrapper>,
        unitLive: LiveData<UnitConverterWrapper>,
        context: Context,
        val isQuantityNameVisibleLive: LiveData<Boolean>,
        private val onUnitSelect: (UnitConverterWrapper) -> Unit) {
    private val quantity = quantityValueLive.value!!.quantity
    val quantityName = context.getSpanned(quantity.nameRes)
    val quantitySymbol = context.getSpanned(quantity.symbolRes)
    val valueLive: LiveData<CharSequence> = MutableLiveData()

    fun updateValue() {
        value = value
    }

    private var value = Double.NaN
        set(value) {
            field = value
            (valueLive as MutableLiveData).value = CustomFormat.format(value)
        }
    private val _units = quantityValueLive.value!!.quantity.units
    val units = _units.map {
        context.getSpanned(it.symbolRes)
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

    fun selectUnit(position: Int) {
        onUnitSelect(_units[position])
    }
}