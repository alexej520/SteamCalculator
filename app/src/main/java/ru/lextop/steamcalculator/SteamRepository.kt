package ru.lextop.steamcalculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import org.jetbrains.anko.doAsync
import ru.lextop.steamcalculator.binding.setValueIfNotSame
import ru.lextop.steamcalculator.db.*
import ru.lextop.steamcalculator.model.*
import steam.quantities.RefractiveIndex
import steam.quantities.Wavelength
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
    val editUnits: Map<QuantityWrapper, LiveData<UnitConverterWrapper>> = (computableQuantities + Wavelength.wrapper).associate {
        val live = MutableLiveData<UnitConverterWrapper>()
        live.value = it.units.first()
        it to live
    }
    val quantityValueLives: Map<QuantityWrapper, LiveData<QuantityValueWrapper>> = allQuantities.filterNot {
        it.quantity == RefractiveIndex || it.quantity == Wavelength
    }.associate {
        val live = MutableLiveData<QuantityValueWrapper>()
        live.value = QuantityValueWrapper(it, Double.NaN, it.units.first())
        it to live
    }.toMutableMap()
    val refractiveIndexQuantityValueLive: LiveData<QuantityValueWrapper> = MutableLiveData<QuantityValueWrapper>().apply {
        val RefractiveIndexWrapper = RefractiveIndex.wrapper
        value = QuantityValueWrapper(RefractiveIndexWrapper, Double.NaN, RefractiveIndexWrapper.units.first())
    }
    val arg1QuantityValueLive: LiveData<QuantityValueWrapper> = MutableLiveData()
    val arg2QuantityValueLive: LiveData<QuantityValueWrapper> = MutableLiveData()
    val wavelengthQuantityValueLive: LiveData<QuantityValueWrapper> = MutableLiveData()

    // Strong refs for GC
    private val viewUnitsLive = steamDao.getViewUnitsLive()
    private val editUnitsLive = steamDao.getEditUnitsLive()
    private val selectedQuantityValue = steamDao.getSelectedQuantityValue()

    init {
        viewUnitsLive.observeForever { list ->
            list!!.toQuantityUnitPairs().forEach { (quantity, unit) ->
                (viewUnits[quantity]!! as MutableLiveData).setValueIfNotSame(unit)
            }
        }
        editUnitsLive.observeForever { list ->
            list!!.toQuantityUnitPairs().forEach { (quantity, unit) ->
                (editUnits[quantity]!! as MutableLiveData).setValueIfNotSame(unit)
            }
        }
        selectedQuantityValue.observeForever {
            val map = it!!.toQuantityValueMap()
            val arg1 = map[ID_ARG1]!!
            val arg2 = map[ID_ARG2]!!
            val wavelength = map[ID_WAVELENGTH]!!
            var updateSteam = false
            arg1QuantityValueLive as MutableLiveData
            updateSteam = updateSteam or arg1QuantityValueLive.setValueIfNotSame(arg1)
            arg2QuantityValueLive as MutableLiveData
            updateSteam = updateSteam or arg2QuantityValueLive.setValueIfNotSame(arg2)
            if (updateSteam) {
                steam = SteamWrapper(arg1, arg2)
                steam.forEach {
                    val quantityValueLive = quantityValueLives[it.quantity]!! as MutableLiveData
                    quantityValueLive.value = it
                }
            }
            wavelengthQuantityValueLive as MutableLiveData
            if (updateSteam or wavelengthQuantityValueLive.setValueIfNotSame(wavelength)) {
                refractiveIndexQuantityValueLive as MutableLiveData
                refractiveIndexQuantityValueLive.value = steam.refractiveIndex(wavelength)
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

    fun setArg1Arg2(arg1: QuantityValueWrapper, arg2: QuantityValueWrapper) {
        doAsync {
            steamDao.insertSelectedQuantityValue(
                    SelectedQuantityValue(ID_ARG1, arg1),
                    SelectedQuantityValue(ID_ARG2, arg2)
            )
        }
    }

    fun setWavelength(wavelength: QuantityValueWrapper) {
        doAsync {
            steamDao.insertSelectedQuantityValue(
                    SelectedQuantityValue(ID_WAVELENGTH, wavelength)
            )
        }
    }
}