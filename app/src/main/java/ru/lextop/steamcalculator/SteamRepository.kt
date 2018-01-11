package ru.lextop.steamcalculator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import org.jetbrains.anko.doAsync
import quantityvalue.Quantity
import quantityvalue.QuantityValue
import quantityvalue.UnitPh
import quantityvalue.invoke
import ru.lextop.steamcalculator.binding.setValueIfNotSame
import ru.lextop.steamcalculator.db.*
import ru.lextop.steamcalculator.model.*
import steam.Steam
import steam.quantities.Pressure
import steam.quantities.RefractiveIndex
import steam.quantities.Temperature
import steam.quantities.Wavelength
import steam.units.K
import steam.units.Pa
import steam.units.ratio
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SteamRepository @Inject constructor
(private val steamDao: SteamDao, private val prefs: SharedPreferences, private val context: Context) {
    private var steam: Steam = Steam(Pressure(Double.NaN, Pa), Temperature(Double.NaN, K))
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
            steam = Steam(first, second).apply {
                forEach {
                    val quantityValueLive = quantityValueLives[it.quantity]!! as MutableLiveData
                    @Suppress("UNCHECKED_CAST")
                    quantityValueLive.value = it
                }
            }
            updateRefractiveIndexQuantityValueLive()
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

    // For Refractive Index

    private val wavelengthQuantityValueLive = MutableLiveData<QuantityValue>().apply {
        val unit = prefs.getString(context.getString(R.string.preferenceKeyWavelengthEditUnit),
                defaultUnits[Wavelength.dimension]!!.symbol
        ).toUnit()
        val v = prefs.getFloat(context.getString(R.string.preferenceKeyWavelengthValue),
                Float.NaN
        ).toDouble()
        value = Wavelength(v, unit)
    }

    private val refractiveIndexQuantityValueLive = MutableLiveData<QuantityValue>().apply {
        value = RefractiveIndex(Double.NaN, ratio)
    }

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            context.getString(R.string.preferenceKeyWavelengthEditUnit) -> {
                val newUnit = prefs.getString(key, defaultUnits[Wavelength.dimension]!!.symbol).toUnit()
                wavelengthQuantityValueLive.value =
                        wavelengthQuantityValueLive.value!!.copy(unit = newUnit)
                updateRefractiveIndexQuantityValueLive()
            }
            context.getString(R.string.preferenceKeyWavelengthValue) -> {
                val newValue = prefs.getFloat(key, Float.NaN).toDouble()
                wavelengthQuantityValueLive.value =
                        wavelengthQuantityValueLive.value!!.copy(value = newValue)
                updateRefractiveIndexQuantityValueLive()
            }
        }
    }

    private fun updateRefractiveIndexQuantityValueLive() {
        refractiveIndexQuantityValueLive.value =
                steam.refractiveIndex(wavelengthQuantityValueLive.value!!)[refractiveIndexQuantityValueLive.value!!.unit]
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    fun setWavelengthQuantityValue(quantityValue: QuantityValue) {
        prefs.edit()
                .putString(
                        context.getString(R.string.preferenceKeyWavelengthEditUnit),
                        quantityValue.unit.symbol
                )
                .putFloat(
                        context.getString(R.string.preferenceKeyWavelengthValue),
                        quantityValue.value.toFloat()
                ).apply()
    }

    fun getWavelengthQuantityValueLive(): LiveData<QuantityValue> = wavelengthQuantityValueLive

    fun getRefractiveIndexQuantityValueLive(): LiveData<QuantityValue> = refractiveIndexQuantityValueLive
}