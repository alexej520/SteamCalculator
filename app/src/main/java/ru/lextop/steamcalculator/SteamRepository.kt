package ru.lextop.steamcalculator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.jetbrains.anko.doAsync
import ru.lextop.steamcalculator.binding.setValueIfNotSame
import ru.lextop.steamcalculator.db.*
import ru.lextop.steamcalculator.steam.*
import ru.lextop.steamcalculator.steam.quantity.DerivedUnit
import ru.lextop.steamcalculator.steam.quantity.Quantity
import ru.lextop.steamcalculator.steam.quantity.QuantityValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SteamRepository @Inject constructor
(private val steamDao: SteamDao) {
     val viewUnits: Map<Quantity, LiveData<DerivedUnit>> = allQuantities.associate {
        val live = MutableLiveData<DerivedUnit>()
        live.value = it.coherentUnit.derived
        it to live
    }
     val editUnits: Map<Quantity, LiveData<DerivedUnit>> = computableProps.associate {
        val live = MutableLiveData<DerivedUnit>()
        live.value = it.coherentUnit.derived
        it to live
    }
    val quantityValueLives: Map<Quantity, LiveData<QuantityValue>> = allQuantities.associate {
        val live = MutableLiveData<QuantityValue>()
        live.value = it(Double.NaN, it.coherentUnit.derived)
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


    fun getEditUnitLive(quantity: Quantity): LiveData<DerivedUnit> =
            editUnits[quantity]!!

    fun setEditUnit(type: Quantity, unit: DerivedUnit) {
        doAsync {
            steamDao.insertEditUnit(EditUnit(type.symbol, unit.name))
        }
    }

    fun getViewUnitLive(quantity: Quantity): LiveData<DerivedUnit> =
            viewUnits[quantity]!!

    fun setViewUnit(quantity: Quantity, unit: DerivedUnit) {
        doAsync {
            steamDao.insertViewUnit(ViewUnit(quantity.symbol, unit.name))
        }
    }

    fun setProperties(first: QuantityValue, second: QuantityValue) {
        doAsync {
            steamDao.insertSelectedProperty(
                    SelectedQuantity(KEY_FIRST_PROP, first),
                    SelectedQuantity(KEY_SECOND_PROP, second)
            )
        }
    }
}