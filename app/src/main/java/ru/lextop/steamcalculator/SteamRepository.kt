package ru.lextop.steamcalculator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import org.jetbrains.anko.doAsync
import ru.lextop.steamcalculator.binding.setValueIfNotSame
import ru.lextop.steamcalculator.db.*
import ru.lextop.steamcalculator.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SteamRepository @Inject constructor
(private val steamDao: SteamDao, private val prefs: SharedPreferences, private val context: Context) {
    private var steam: SteamWrapper = SteamWrapper()
    val viewUnits: Map<QuantityWrapper, LiveData<UnitConverterWrapper>> = allQuantities.associate {
        val live = MutableLiveData<UnitConverterWrapper>()
        live.value = it.units.first()
        it to live
    }
    val editUnits: Map<QuantityWrapper, LiveData<UnitConverterWrapper>> = computableQuantities.associate {
        val live = MutableLiveData<UnitConverterWrapper>()
        live.value = it.units.first()
        it to live
    }
    val quantityValueLives: Map<QuantityWrapper, LiveData<QuantityValueWrapper>> = allQuantities.associate {
        val live = MutableLiveData<QuantityValueWrapper>()
        live.value = QuantityValueWrapper(it, Double.NaN, it.units.first())
        it to live
    }.toMutableMap()
    val firstQuantityValueLive: LiveData<QuantityValueWrapper> = MutableLiveData()
    val secondQuantityValueLive: LiveData<QuantityValueWrapper> = MutableLiveData()

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
            val first = map[ID_ARG1]!!
            val second = map[ID_ARG2]!!
            firstQuantityValueLive as MutableLiveData
            firstQuantityValueLive.value = first
            secondQuantityValueLive as MutableLiveData
            secondQuantityValueLive.value = second
            steam = SteamWrapper(first, second)
            steam.forEach {
                val quantityValueLive = quantityValueLives[it.quantity]!! as MutableLiveData
                quantityValueLive.value = it
            }
        }
    }


    fun getEditUnitLive(quantity: QuantityWrapper): LiveData<UnitConverterWrapper> =
            editUnits[quantity]!!

    fun setEditUnit(quantity: QuantityWrapper, unit: UnitConverterWrapper) {
        doAsync {
            steamDao.insertEditUnit(EditUnit(quantity.id, unit.id))
        }
    }

    fun getViewUnitLive(quantity: QuantityWrapper): LiveData<UnitConverterWrapper> =
            viewUnits[quantity]!!

    fun setViewUnit(quantity: QuantityWrapper, unit: UnitConverterWrapper) {
        doAsync {
            steamDao.insertViewUnit(ViewUnit(quantity.id, unit.id))
        }
    }

    fun setQuantityValues(first: QuantityValueWrapper, second: QuantityValueWrapper) {
        doAsync {
            steamDao.insertSelectedQuantityValue(
                    SelectedQuantityValue(ID_ARG1, first),
                    SelectedQuantityValue(ID_ARG2, second)
            )
        }
    }
}