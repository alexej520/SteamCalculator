package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.*
import android.content.Context
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.steam.quantity.DerivedUnit
import ru.lextop.steamcalculator.steam.quantity.QuantityValue

open class QuantityViewModel(
        quantityValueLive: LiveData<QuantityValue>,
        unitLive: LiveData<DerivedUnit>,
        context: Context,
        val isPropNameVisibleLive: LiveData<Boolean>,
        private val repo: SteamRepository)
    : ViewModel() {
    private val prop = quantityValueLive.value!!.quantity
    val propName = context.getSpanned(prop.nameId)
    val propSymbol = context.getSpanned(prop.symbolId)
    val valueLive: LiveData<CharSequence> = MutableLiveData()
    var value = Double.NaN
        set(value) {
            field = value
            (valueLive as MutableLiveData).value = CustomFormat.format(value)
        }
    private val _units = quantityValueLive.value!!.quantity.unitList
    val units = _units.map {
        context.getSpanned(it.id)
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
        repo.setViewUnit(prop, _units[id])
    }
}