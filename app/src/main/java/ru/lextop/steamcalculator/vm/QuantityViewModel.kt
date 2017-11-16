package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.*
import android.content.Context
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.steam.quantity.UnitPh
import ru.lextop.steamcalculator.steam.quantity.Quantity
import ru.lextop.steamcalculator.unitList

open class QuantityViewModel(
        quantityLive: LiveData<Quantity>,
        unitLive: LiveData<UnitPh>,
        context: Context,
        val isPropNameVisibleLive: LiveData<Boolean>,
        private val repo: SteamRepository)
    : ViewModel() {
    private val prop = quantityLive.value!!.property
    val propName = context.getSpanned(prop.nameId)
    val propSymbol = context.getSpanned(prop.symbolId)
    val valueLive: LiveData<CharSequence> = MutableLiveData()
    var value = Double.NaN
        set(value) {
            field = value
            (valueLive as MutableLiveData).value = CustomFormat.format(value)
        }
    private val _units = quantityLive.value!!.property.unitList
    val units = _units.map {
        context.getSpanned(it.id)
    }
    val unitSelectionLive: LiveData<Int> = MutableLiveData()

    init {
        quantityLive.observeForever {
            value = it!![unitLive.value!!].value
        }
        unitLive.observeForever { unit ->
            unitSelectionLive as MutableLiveData
            unitSelectionLive.value = _units.indexOf(unit!!)
            value = quantityLive.value!![unit].value
        }
    }

    fun selectUnit(id: Int) {
        repo.setViewUnit(prop, _units[id])
    }
}