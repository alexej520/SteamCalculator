package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.SharedPreferences
import quantityvalue.Quantity
import quantityvalue.QuantityValue
import quantityvalue.UnitPh
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.binding.nullIfNotInitialized
import ru.lextop.steamcalculator.model.*
import javax.inject.Inject

class SteamViewModel @Inject constructor
(private val repo: SteamRepository, private val context: Context, private val prefs: SharedPreferences) : ViewModel() {
    val firstInputFocusLive: LiveData<Boolean> = MutableLiveData()
    val secondInputFocusLive: LiveData<Boolean> = MutableLiveData()

    val quantityValueModels: List<QuantityValueViewModel>
    private val firstQuantities: List<Quantity> = computablePropMap.keys.toList()
    val firstQuantityNameLive: LiveData<CharSequence> = MutableLiveData()
    val secondQuantityNameLive: LiveData<CharSequence> = MutableLiveData()
    val firstQuantityNameToSymbolList: List<Pair<CharSequence, CharSequence>> = firstQuantities.map {
        context.getSpanned(it.symbolId) to context.getSpanned(it.nameId)
    }
    private var secondQuantities: List<Quantity> = listOf()
        set(value) {
            field = value
            (secondQuantityNameToSymbolListLive as MutableLiveData).value = value.map {
                context.getSpanned(it.symbolId) to context.getSpanned(it.nameId)
            }
        }
    val secondQuantityNameToSymbolListLive: LiveData<List<Pair<CharSequence, CharSequence>>> = MutableLiveData()
    val firstQuantitySelectionLive: LiveData<Int> = MutableLiveData()
    val secondQuantitySelectionLive: LiveData<Int> = MutableLiveData()
    var isQuantityNameVisibleLive: LiveData<Boolean> = MutableLiveData()
    private lateinit var firstQuantityValue: QuantityValue
    private lateinit var secondQuantityValue: QuantityValue
    private var firstUnitLive: LiveData<UnitPh> = MutableLiveData()
    private var secondUnitLive: LiveData<UnitPh> = MutableLiveData()
    val firstUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    val secondUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    private var firstUnits: List<UnitPh> = listOf()
        set(value) {
            field = value
            firstUnitsLive as MutableLiveData
            firstUnitsLive.value = value.map { context.getSpanned(it.symbolId) }
        }
    private var secondUnits: List<UnitPh> = listOf()
        set(value) {
            field = value
            secondUnitsLive as MutableLiveData
            secondUnitsLive.value = value.map { context.getSpanned(it.symbolId) }
        }
    val firstUnitSelectionLive: LiveData<Int> = MutableLiveData()
    val secondUnitSelectionLive: LiveData<Int> = MutableLiveData()
    val firstValueLive: LiveData<CharSequence> = MutableLiveData()
    val secondValueLive: LiveData<CharSequence> = MutableLiveData()
    private var firstValue: Double = Double.NaN
        set(value) {
            field = value
            (firstValueLive as MutableLiveData).value = doubleToInputValue(value)
        }
    private var secondValue: Double = Double.NaN
        set(value) {
            field = value
            (secondValueLive as MutableLiveData).value = doubleToInputValue(value)
        }

    private val firstUnitObserver = Observer<UnitPh> { unit ->
        (firstUnitSelectionLive as MutableLiveData).value = firstUnits.indexOf(unit)
        firstValue = firstQuantityValue[unit!!].value
        (secondInputFocusLive as MutableLiveData).value = false
        (firstInputFocusLive as MutableLiveData).value = true
    }
    private val secondUnitObserver = Observer<UnitPh> { unit ->
        (secondUnitSelectionLive as MutableLiveData).value = secondUnits.indexOf(unit)
        secondValue = secondQuantityValue[unit!!].value
        if (secondUnitSelectedByUser) {
            (firstInputFocusLive as MutableLiveData).value = false
            (secondInputFocusLive as MutableLiveData).value = true
        } else {
            secondUnitSelectedByUser = true
        }
    }
    private var secondQuantitySelectedByUser = false
    private var secondUnitSelectedByUser = false
    private val firstQuantityObserver = Observer<QuantityValue> { quantityValue ->
        val newQuantity = quantityValue!!.quantity
        val oldQuantity = nullIfNotInitialized { firstQuantityValue.quantity }
        firstQuantityValue = quantityValue

        if (newQuantity != oldQuantity) {
            (firstQuantityNameLive as MutableLiveData).value = context.getSpanned(newQuantity.nameId)
            (firstQuantitySelectionLive as MutableLiveData).value = firstQuantities.indexOf(newQuantity)
            firstUnits = newQuantity.dimension.unitList
            firstUnitLive.removeObserver(firstUnitObserver)
            firstUnitLive = repo.getEditUnitLive(newQuantity)
            firstUnitLive.observeForever(firstUnitObserver)
            firstValue = quantityValue[firstUnitLive.value!!].value
            secondQuantitySelectedByUser = false
            secondQuantities = computablePropMap[newQuantity]!!
        }
    }
    private val secondQuantityObserver = Observer<QuantityValue> { quantityValue ->
        val newQuantity = quantityValue!!.quantity
        val oldQuantity = nullIfNotInitialized { secondQuantityValue.quantity }
        secondQuantityValue = quantityValue
        (secondQuantitySelectionLive as MutableLiveData).value = secondQuantities.indexOf(newQuantity)
        if (!secondQuantitySelectedByUser) {
            secondQuantitySelectedByUser = true
            secondUnitSelectedByUser = false
        }
        if (newQuantity != oldQuantity) {
            (secondQuantityNameLive as MutableLiveData).value = context.getSpanned(newQuantity.nameId)
            secondUnits = newQuantity.dimension.unitList
            secondUnitLive.removeObserver(secondUnitObserver)
            secondUnitLive = repo.getEditUnitLive(newQuantity)
            secondUnitLive.observeForever(secondUnitObserver)
            secondValue = quantityValue[secondUnitLive.value!!].value
        }
    }

    init {
        (isQuantityNameVisibleLive as MutableLiveData).value =
                prefs.getBoolean(context.getString(R.string.preferenceKeyShowPropertyNames), false)
        CustomFormat.maxSymbols = prefs.getInt(context.getString(R.string.preferenceKeyDecimals), 5)
        CustomFormat.scientificFormatOnly = prefs.getBoolean(context.getString(R.string.preferenceKeyScientificFormatOnly), false)
        quantityValueModels = repo.quantityValueLives.values.map { quantityLive ->
            QuantityValueViewModel(
                    quantityLive,
                    repo.getViewUnitLive(quantityLive.value!!.quantity),
                    context,
                    isQuantityNameVisibleLive,
                    repo)
        }
        repo.firstQuantityValueLive.observeForever(firstQuantityObserver)
        repo.secondQuantityValueLive.observeForever(secondQuantityObserver)
    }

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            context.getString(R.string.preferenceKeyShowPropertyNames) ->
                (isQuantityNameVisibleLive as MutableLiveData).value = prefs.getBoolean(key, false)
            context.getString(R.string.preferenceKeyScientificFormatOnly) -> {
                CustomFormat.scientificFormatOnly = prefs.getBoolean(key, false)
                firstValue = firstValue
                secondValue = secondValue
                quantityValueModels.forEach { it.updateValue() }
            }
            context.getString(R.string.preferenceKeyDecimals) -> {
                CustomFormat.maxSymbols = prefs.getInt(key, 4)
                firstValue = firstValue
                secondValue = secondValue
                quantityValueModels.forEach { it.updateValue() }
            }
            context.getString(R.string.preferenceKeyUnitSet) -> {
                val unitSet = unitSetMap.mapKeys { context.getString(it.key) }.get(
                        prefs.getString(key, context.getString(R.string.unitSetDefaultValue)))
                if (unitSet != null) {
                    computableQuantities.forEach {
                        repo.setEditUnit(it, unitSet[it.dimension]!!)
                    }
                    allQuantities.forEach {
                        repo.setViewUnit(it, unitSet[it.dimension]!!)
                    }
                }
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onCleared() {
        repo.firstQuantityValueLive.removeObserver(firstQuantityObserver)
        repo.secondQuantityValueLive.removeObserver(secondQuantityObserver)
        firstUnitLive.removeObserver(firstUnitObserver)
        secondUnitLive.removeObserver(secondUnitObserver)
    }

    private fun doubleToInputValue(double: Double): CharSequence =
            CustomFormat.formatIgnoreNaN(double)

    private fun inputValueToDouble(input: CharSequence): Double =
            CustomFormat.parse(input.toString())

    fun inputFirstValue(input: CharSequence) {
        repo.setQuantityValues(
                firstQuantityValue.copy(
                        value = inputValueToDouble(input),
                        unit = firstUnitLive.value!!),
                secondQuantityValue)
    }

    fun inputSecondPropValue(input: CharSequence) {
        repo.setQuantityValues(
                firstQuantityValue,
                secondQuantityValue.copy(
                        value = inputValueToDouble(input),
                        unit = secondUnitLive.value!!))
    }

    fun selectFirstQuantity(index: Int) {
        nullIfNotInitialized { firstQuantityValue } ?: return
        val oldProp = firstQuantityValue.quantity
        val newProp = firstQuantities[index]
        val newSecondProps = computablePropMap[newProp]!!
        val oldSecondProp = nullIfNotInitialized { secondQuantityValue.quantity }
        val newSecondProp = when {
            newProp == oldSecondProp -> oldProp
            oldSecondProp in newSecondProps -> oldSecondProp
            oldProp in newSecondProps -> oldProp
            else -> newSecondProps.first()
        }!!
        repo.setQuantityValues(repo.quantityValueLives[newProp]!!.value!!, repo.quantityValueLives[newSecondProp]!!.value!!)
    }

    fun selectSecondProp(index: Int) {
        val newType = secondQuantities[index]
        repo.setQuantityValues(firstQuantityValue, repo.quantityValueLives[newType]!!.value!!)
    }

    fun selectFirstUnit(index: Int) {
        repo.setEditUnit(firstQuantityValue.quantity, firstUnits[index])
    }

    fun selectSecondUnit(index: Int) {
        repo.setEditUnit(secondQuantityValue.quantity, secondUnits[index])
    }
}