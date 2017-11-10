package ru.lextop.steamcalculator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.jetbrains.anko.doAsync
import ru.lextop.steamcalculator.binding.setValueIfNotSame
import ru.lextop.steamcalculator.db.*
import ru.lextop.steamcalculator.steam.*
import ru.lextop.steamcalculator.steam.quantity.DerivativeUnit
import ru.lextop.steamcalculator.steam.quantity.Property
import ru.lextop.steamcalculator.steam.quantity.Quantity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SteamRepository @Inject constructor
(private val steamDao: SteamDao) {
    private val viewUnits: Map<Property, LiveData<DerivativeUnit>> = props.associate {
        val live = MutableLiveData<DerivativeUnit>()
        live.value = it.baseUnit.alias
        it to live
    }
    private val editUnits: Map<Property, LiveData<DerivativeUnit>> = props.associate {
        val live = MutableLiveData<DerivativeUnit>()
        live.value = it.baseUnit.alias
        it to live
    }
    val quantityLives: Map<Property, LiveData<Quantity>> = props.associate {
        val live = MutableLiveData<Quantity>()
        live.value = it(Double.NaN, it.baseUnit.alias)
        it to live
    }.toMutableMap()
    val firstQuantityLive: LiveData<Quantity> = MutableLiveData()
    val secondQuantityLive: LiveData<Quantity> = MutableLiveData()

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
            firstQuantityLive as MutableLiveData
            firstQuantityLive.value = first
            secondQuantityLive as MutableLiveData
            secondQuantityLive.value = second
            val steam = Steam(first, second)
            steam.forEach {
                val propLive = quantityLives[it.property]!! as MutableLiveData
                @Suppress("UNCHECKED_CAST")
                propLive.value = it
            }
        }
    }


    fun getEditUnitLive(type: Property): LiveData<DerivativeUnit> =
            editUnits[type]!!

    fun setEditUnit(type: Property, unit: DerivativeUnit) {
        doAsync { steamDao.insertEditUnit(EditUnit(type.symbol, unit.name)) }
    }

    fun getViewUnitLive(type: Property): LiveData<DerivativeUnit> =
            viewUnits[type]!!

    fun setViewUnit(type: Property, unit: DerivativeUnit) {
        doAsync { steamDao.insertViewUnit(ViewUnit(type.symbol, unit.name)) }
    }

    fun setProperties(first: Quantity, second: Quantity) {
        doAsync {
            steamDao.insertSelectedProperty(
                    SelectedQuantity(KEY_FIRST_PROP, first),
                    SelectedQuantity(KEY_SECOND_PROP, second)
            )
        }
    }
}