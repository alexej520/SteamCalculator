package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.*
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.steam.quantity.DerivativeUnit
import ru.lextop.steamcalculator.steam.quantity.Quantity
import ru.lextop.steamcalculator.unitList

open class QuantityViewModel(
        quantityLive: LiveData<Quantity>,
        unitLive: LiveData<DerivativeUnit>,
        private val repo: SteamRepository)
    : ViewModel() {
    private val prop = quantityLive.value!!.property
    val propName = prop.name
    val propSymbol = prop.symbol
    val valueLive: LiveData<CharSequence> = MutableLiveData()
    val units = quantityLive.value!!.property.unitList
    val unitSelectionLive: LiveData<Int> = MutableLiveData()

    init {
        fun updateLiveValue(unit: DerivativeUnit) {
            valueLive as MutableLiveData
            valueLive.value = String.format("%.1f", quantityLive.value!![unit].value)
        }
        quantityLive.observeForever {
            updateLiveValue(unitLive.value!!)
        }
        unitLive.observeForever { unit ->
            unitSelectionLive as MutableLiveData
            unitSelectionLive.value = units.indexOf(unit!!)
            updateLiveValue(unit)
        }
    }

    fun selectUnit(id: Int) {
        repo.setViewUnit(prop, units[id])
    }
}