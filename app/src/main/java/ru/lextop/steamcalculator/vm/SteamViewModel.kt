package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.SharedPreferences
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.binding.nullIfNotInitialized
import ru.lextop.steamcalculator.model.*
import steam.quantities.RefractiveIndex
import steam.quantities.Wavelength
import javax.inject.Inject

class SteamViewModel @Inject constructor
(private val repo: SteamRepository, private val context: Context, private val prefs: SharedPreferences) : ViewModel() {
    val firstInputFocusLive: LiveData<Boolean> = MutableLiveData()
    val secondInputFocusLive: LiveData<Boolean> = MutableLiveData()

    val quantityValueViewModels: List<QuantityValueViewModel>
    val refractiveIndexViewModel: RefractiveIndexViewModel
    private val firstQuantities: List<QuantityWrapper> = computablePropMap.keys.toList()
    val firstQuantityNameLive: LiveData<CharSequence> = MutableLiveData()
    val secondQuantityNameLive: LiveData<CharSequence> = MutableLiveData()
    val firstQuantityNameToSymbolList: List<Pair<CharSequence, CharSequence>> = firstQuantities.map {
        context.getSpanned(it.symbolRes) to context.getSpanned(it.nameRes)
    }
    private var secondQuantities: List<QuantityWrapper> = listOf()
        set(value) {
            field = value
            (secondQuantityNameToSymbolListLive as MutableLiveData).value = value.map {
                context.getSpanned(it.symbolRes) to context.getSpanned(it.nameRes)
            }
        }
    val secondQuantityNameToSymbolListLive: LiveData<List<Pair<CharSequence, CharSequence>>> = MutableLiveData()
    val firstQuantitySelectionLive: LiveData<Int> = MutableLiveData()
    val secondQuantitySelectionLive: LiveData<Int> = MutableLiveData()
    var isQuantityNameVisibleLive: LiveData<Boolean> = MutableLiveData()
    private lateinit var firstQuantityValue: QuantityValueWrapper
    private lateinit var secondQuantityValue: QuantityValueWrapper
    private var firstUnitLive: LiveData<UnitConverterWrapper> = MutableLiveData()
    private var secondUnitLive: LiveData<UnitConverterWrapper> = MutableLiveData()
    val firstUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    val secondUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    private var firstUnits: List<UnitConverterWrapper> = listOf()
        set(value) {
            field = value
            firstUnitsLive as MutableLiveData
            firstUnitsLive.value = value.map { context.getSpanned(it.symbolRes) }
        }
    private var secondUnits: List<UnitConverterWrapper> = listOf()
        set(value) {
            field = value
            secondUnitsLive as MutableLiveData
            secondUnitsLive.value = value.map { context.getSpanned(it.symbolRes) }
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

    private val firstUnitObserver = Observer<UnitConverterWrapper> { unit ->
        (firstUnitSelectionLive as MutableLiveData).value = firstUnits.indexOf(unit)
        firstValue = firstQuantityValue[unit!!].value
        (secondInputFocusLive as MutableLiveData).value = false
        (firstInputFocusLive as MutableLiveData).value = true
    }
    private val secondUnitObserver = Observer<UnitConverterWrapper> { unit ->
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
    private val firstQuantityObserver = Observer<QuantityValueWrapper> { quantityValue ->
        val newQuantity = quantityValue!!.quantity
        val oldQuantity = nullIfNotInitialized { firstQuantityValue.quantity }
        firstQuantityValue = quantityValue

        if (newQuantity != oldQuantity) {
            (firstQuantityNameLive as MutableLiveData).value = context.getSpanned(newQuantity.nameRes)
            (firstQuantitySelectionLive as MutableLiveData).value = firstQuantities.indexOf(newQuantity)
            firstUnits = newQuantity.units
            firstUnitLive.removeObserver(firstUnitObserver)
            firstUnitLive = repo.getEditUnitLive(newQuantity)
            firstUnitLive.observeForever(firstUnitObserver)
            firstValue = quantityValue[firstUnitLive.value!!].value
            secondQuantitySelectedByUser = false
            secondQuantities = computablePropMap[newQuantity]!!
        }
    }
    private val secondQuantityObserver = Observer<QuantityValueWrapper> { quantityValue ->
        val newQuantity = quantityValue!!.quantity
        val oldQuantity = nullIfNotInitialized { secondQuantityValue.quantity }
        secondQuantityValue = quantityValue
        (secondQuantitySelectionLive as MutableLiveData).value = secondQuantities.indexOf(newQuantity)
        if (!secondQuantitySelectedByUser) {
            secondQuantitySelectedByUser = true
            secondUnitSelectedByUser = false
        }
        if (newQuantity != oldQuantity) {
            (secondQuantityNameLive as MutableLiveData).value = context.getSpanned(newQuantity.nameRes)
            secondUnits = newQuantity.units
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
        quantityValueViewModels = repo.quantityValueLives.values
                .map { quantityLive ->
            val quantity = quantityLive.value!!.quantity
            QuantityValueViewModel(
                    quantityLive,
                    repo.getViewUnitLive(quantity),
                    context,
                    isQuantityNameVisibleLive,
                    { repo.setViewUnit(quantity, it) })
        }
        repo.arg1QuantityValueLive.observeForever(firstQuantityObserver)
        repo.arg2QuantityValueLive.observeForever(secondQuantityObserver)
        val refractiveIndexWrapper = RefractiveIndex.wrapper
        refractiveIndexViewModel = RefractiveIndexViewModel(
                context = context,
                isQuantityNameVisibleLive = isQuantityNameVisibleLive,
                refractiveIndexQuantityValueViewModel = QuantityValueViewModel(
                repo.refractiveIndexQuantityValueLive,
                repo.getViewUnitLive(refractiveIndexWrapper),
                context,
                isQuantityNameVisibleLive,
                { repo.setViewUnit(refractiveIndexWrapper, it) }),
                wavelengthQuantityValueLive = repo.wavelengthQuantityValueLive,
                wavelengthUnitLive = repo.getEditUnitLive(Wavelength.wrapper),
                onWavelengthUnitSelected = { repo.setEditUnit(refractiveIndexWrapper, it)},
                onWavelengthQuantityValueChanged = { repo.setWavelength(it) })
    }

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            context.getString(R.string.preferenceKeyShowPropertyNames) ->
                (isQuantityNameVisibleLive as MutableLiveData).value = prefs.getBoolean(key, false)
            context.getString(R.string.preferenceKeyScientificFormatOnly) -> {
                CustomFormat.scientificFormatOnly = prefs.getBoolean(key, false)
                updateValues()
            }
            context.getString(R.string.preferenceKeyDecimals) -> {
                CustomFormat.maxSymbols = prefs.getInt(key, 4)
                updateValues()
            }
            context.getString(R.string.preferenceKeyUnitSet) -> {
                val unitSystem = DefaultUnits.getUnitSystem(context, prefs.getString(key, ""))
                if (unitSystem != null) {
                    computableQuantities.forEach {
                        repo.setEditUnit(it, unitSystem(it.defaultUnits))
                    }
                    allQuantities.forEach {
                        repo.setViewUnit(it, unitSystem(it.defaultUnits))
                    }
                }
            }
        }
    }

    fun updateValues() {
        firstValue = firstValue
        secondValue = secondValue
        quantityValueViewModels.forEach { it.updateValue() }
        refractiveIndexViewModel.updateValue()
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onCleared() {
        repo.arg1QuantityValueLive.removeObserver(firstQuantityObserver)
        repo.arg2QuantityValueLive.removeObserver(secondQuantityObserver)
        firstUnitLive.removeObserver(firstUnitObserver)
        secondUnitLive.removeObserver(secondUnitObserver)
        refractiveIndexViewModel.onCleared()
    }

    private fun doubleToInputValue(double: Double): CharSequence =
            CustomFormat.formatIgnoreNaN(double)

    private fun inputValueToDouble(input: CharSequence): Double =
            CustomFormat.parse(input.toString())

    fun inputFirstValue(input: CharSequence) {
        repo.setArg1Arg2(
                QuantityValueWrapper(
                        quantity = firstQuantityValue.quantity,
                        value = inputValueToDouble(input),
                        unit = firstUnitLive.value!!),
                secondQuantityValue)
    }

    fun inputSecondPropValue(input: CharSequence) {
        repo.setArg1Arg2(
                firstQuantityValue,
                QuantityValueWrapper(
                        quantity = secondQuantityValue.quantity,
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
        repo.setArg1Arg2(repo.quantityValueLives[newProp]!!.value!!, repo.quantityValueLives[newSecondProp]!!.value!!)
    }

    fun selectSecondProp(index: Int) {
        val newType = secondQuantities[index]
        repo.setArg1Arg2(firstQuantityValue, repo.quantityValueLives[newType]!!.value!!)
    }

    fun selectFirstUnit(index: Int) {
        repo.setEditUnit(firstQuantityValue.quantity, firstUnits[index])
    }

    fun selectSecondUnit(index: Int) {
        repo.setEditUnit(secondQuantityValue.quantity, secondUnits[index])
    }
}