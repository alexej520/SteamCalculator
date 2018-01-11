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
            list!!.toQuantityUnitPairs().forEach { (quantity, unit) ->
                (viewUnits[quantity]!! as MutableLiveData).setValueIfNotSame(unit)
            }
        }
        steamDao.getEditUnitsLive().observeForever { list ->
            list!!.toQuantityUnitPairs().forEach { (quantity, unit) ->
                (editUnits[quantity]!! as MutableLiveData).setValueIfNotSame(unit)
            }
        }
        steamDao.getSelectedQuantityValue().observeForever {
            val map = it!!.toQuantityMap()
            val first = map[KEY_FIRST_QUANTITY]!!
            val second = map[KEY_SECOND_QUANTITY]!!
            firstQuantityValueLive as MutableLiveData
            firstQuantityValueLive.value = first
            secondQuantityValueLive as MutableLiveData
            secondQuantityValueLive.value = second
            val steam = Steam(first, second)
            steam.forEach {
                val quantityValueLive = quantityValueLives[it.quantity]!! as MutableLiveData
                @Suppress("UNCHECKED_CAST")
                quantityValueLive.value = it
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

    fun setQuantityValues(first: QuantityValue, second: QuantityValue) {
        doAsync {
            steamDao.insertSelectedQuantityValue(
                    SelectedQuantityValue(KEY_FIRST_QUANTITY, first),
                    SelectedQuantityValue(KEY_SECOND_QUANTITY, second)
            )
        }
    }
}