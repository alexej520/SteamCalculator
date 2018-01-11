package ru.lextop.steamcalculator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.jetbrains.anko.doAsync
import ru.lextop.steamcalculator.binding.setValueIfNotSame
import ru.lextop.steamcalculator.db.*
import quantityvalue.*
import steam.Steam
import ru.lextop.steamcalculator.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SteamRepository @Inject constructor
(private val steamDao: SteamDao) {
     val viewUnits: Map<Quantity, LiveData<UnitPh>> = allQuantities.associate {
        val live = MutableLiveData<UnitPh>()
        live.value = it.dimension.unitList.first()
        it to live
    }
     val editUnits: Map<Quantity, LiveData<UnitPh>> = computableQuantities.associate {
        val live = MutableLiveData<UnitPh>()
        live.value = it.dimension.unitList.first()
        it to live
    }
    val quantityValueLives: Map<Quantity, LiveData<QuantityValue>> = allQuantities.associate {
        val live = MutableLiveData<QuantityValue>()
        live.value = it(Double.NaN, it.dimension.unitList.first())
        it to live
    }.toMutableMap()
    val firstQuantityValueLive: LiveData<QuantityValue> = MutableLiveData()
    val secondQuantityValueLive: LiveData<QuantityValue> = MutableLiveData()

    init {
        steamDao.getViewUnitsLive().observeForever { list ->
            list!!.toPropUnitList().forEach { (propType, unit) ->
                (viewUnits[propType]!! as MutableLiveData).setValueIfNotSame(unit)
            }
        }
        steamDao.getEditUnitsLive().observeForever { list ->
            list!!.toPropUnitList().forEach { (propType, unit) ->
                (editUnits[propType]!! as MutableLiveData).setValueIfNotSame(unit)
            }
        }
        steamDao.getSelectedProperty().observeForever {
            val map = it!!.toQuantityMap()
            val first = map[KEY_FIRST_PROP]!!
            val second = map[KEY_SECOND_PROP]!!
            firstQuantityValueLive as MutableLiveData
            firstQuantityValueLive.value = first
            secondQuantityValueLive as MutableLiveData
            secondQuantityValueLive.value = second
            val steam = Steam(first, second)
            steam.forEach {
                val propLive = quantityValueLives[it.quantity]!! as MutableLiveData
                @Suppress("UNCHECKED_CAST")
                propLive.value = it
            }
        }
    }


    fun getEditUnitLive(quantity: Quantity): LiveData<UnitPh> =
            editUnits[quantity]!!

    fun setEditUnit(type: Quantity, unit: UnitPh) {
        doAsync {
            steamDao.insertEditUnit(EditUnit(type.symbol, unit.symbol))
        }
    }

    fun getViewUnitLive(quantity: Quantity): LiveData<UnitPh> =
            viewUnits[quantity]!!

    fun setViewUnit(quantity: Quantity, unit: UnitPh) {
        doAsync {
            steamDao.insertViewUnit(ViewUnit(quantity.symbol, unit.symbol))
        }
    }

    fun setProperties(first: QuantityValue, second: QuantityValue) {
        doAsync {
            steamDao.insertSelectedProperty(
                    SelectedQuantityValue(KEY_FIRST_PROP, first),
                    SelectedQuantityValue(KEY_SECOND_PROP, second)
            )
        }
    }
}