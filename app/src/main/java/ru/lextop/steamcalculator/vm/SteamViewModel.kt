package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.SharedPreferences
import ru.lextop.steamcalculator.*
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.binding.nullIfNotInitialized
import ru.lextop.steamcalculator.steam.quantity.DerivedUnit
import ru.lextop.steamcalculator.steam.quantity.Quantity
import ru.lextop.steamcalculator.steam.quantity.QuantityValue
import javax.inject.Inject

class SteamViewModel @Inject constructor
(private val repo: SteamRepository, private val context: Context, private val prefs: SharedPreferences) : ViewModel() {
    val firstInputFocusLive: LiveData<Boolean> = MutableLiveData()
    val secondInputFocusLive: LiveData<Boolean> = MutableLiveData()

    val quantityModels: List<QuantityViewModel>
    private val firstProps: List<Quantity> = COMPUTABLE_PROP_MAP.keys.toList()
    val firstPropNameLive: LiveData<CharSequence> = MutableLiveData()
    val secondPropNameLive: LiveData<CharSequence> = MutableLiveData()
    val firstPropNameToSymbolList: List<Pair<CharSequence, CharSequence>> = firstProps.map {
        context.getSpanned(it.symbolId) to context.getSpanned(it.nameId)
    }
    private var secondProps: List<Quantity> = listOf()
        set(value) {
            field = value
            (secondPropNameToSymbolListLive as MutableLiveData).value = value.map {
                context.getSpanned(it.symbolId) to context.getSpanned(it.nameId)
            }
        }
    val secondPropNameToSymbolListLive: LiveData<List<Pair<CharSequence, CharSequence>>> = MutableLiveData()
    val firstPropSelectionLive: LiveData<Int> = MutableLiveData()
    val secondPropSelectionLive: LiveData<Int> = MutableLiveData()
    var isPropNameVisibleLive: LiveData<Boolean> = MutableLiveData()
    private lateinit var firstQuantityValue: QuantityValue
    private lateinit var secondQuantityValue: QuantityValue
    private var firstUnitLive: LiveData<DerivedUnit> = MutableLiveData()
    private var secondUnitLive: LiveData<DerivedUnit> = MutableLiveData()
    val firstUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    val secondUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    private var firstUnits: List<DerivedUnit> = listOf()
        set(value) {
            field = value
            firstUnitsLive as MutableLiveData
            firstUnitsLive.value = value.map { context.getSpanned(it.id) }
        }
    private var secondUnits: List<DerivedUnit> = listOf()
        set(value) {
            field = value
            secondUnitsLive as MutableLiveData
            secondUnitsLive.value = value.map { context.getSpanned(it.id) }
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

    private val firstUnitObserver = Observer<DerivedUnit> { unit ->
        (firstUnitSelectionLive as MutableLiveData).value = firstUnits.indexOf(unit)
        firstValue = firstQuantityValue[unit!!].value
        (secondInputFocusLive as MutableLiveData).value = false
        (firstInputFocusLive as MutableLiveData).value = true
    }
    private val secondUnitObserver = Observer<DerivedUnit> { unit ->
        (secondUnitSelectionLive as MutableLiveData).value = secondUnits.indexOf(unit)
        secondValue = secondQuantityValue[unit!!].value
        if (secondUnitSelectedByUser) {
            (firstInputFocusLive as MutableLiveData).value = false
            (secondInputFocusLive as MutableLiveData).value = true
        } else {
            secondUnitSelectedByUser = true
        }
    }
    private var secondPropSelectedByUser = false
    private var secondUnitSelectedByUser = false
    private val firstQuantityObserver = Observer<QuantityValue> { p ->
        val newProp = p!!.quantity
        val oldProp = nullIfNotInitialized { firstQuantityValue.quantity }
        firstQuantityValue = p

        if (newProp != oldProp) {
            (firstPropNameLive as MutableLiveData).value = context.getSpanned(newProp.nameId)
            (firstPropSelectionLive as MutableLiveData).value = firstProps.indexOf(newProp)
            firstUnits = newProp.unitList
            firstUnitLive.removeObserver(firstUnitObserver)
            firstUnitLive = repo.getEditUnitLive(newProp)
            firstUnitLive.observeForever(firstUnitObserver)
            firstValue = p[firstUnitLive.value!!].value
            secondPropSelectedByUser = false
            secondProps = COMPUTABLE_PROP_MAP[newProp]!!
        }
    }
    private val secondQuantityObserver = Observer<QuantityValue> { p ->
        val newProp = p!!.quantity
        val oldProp = nullIfNotInitialized { secondQuantityValue.quantity }
        secondQuantityValue = p
        (secondPropSelectionLive as MutableLiveData).value = secondProps.indexOf(newProp)
        if (!secondPropSelectedByUser) {
            secondPropSelectedByUser = true
            secondUnitSelectedByUser = false
        }
        if (newProp != oldProp) {
            (secondPropNameLive as MutableLiveData).value = context.getSpanned(newProp.nameId)
            secondUnits = newProp.unitList
            secondUnitLive.removeObserver(secondUnitObserver)
            secondUnitLive = repo.getEditUnitLive(newProp)
            secondUnitLive.observeForever(secondUnitObserver)
            secondValue = p[secondUnitLive.value!!].value
        }
    }

    init {
        (isPropNameVisibleLive as MutableLiveData).value =
                prefs.getBoolean(context.getString(R.string.preferenceKeyShowPropertyNames), false)
        CustomFormat.maxSymbols = prefs.getInt(context.getString(R.string.preferenceKeyDecimals), 5)
        CustomFormat.scientificFormatOnly = prefs.getBoolean(context.getString(R.string.preferenceKeyScientificFormatOnly), false)
        quantityModels = repo.quantityValueLives.values.map { quantityLive ->
            QuantityViewModel(
                    quantityLive,
                    repo.getViewUnitLive(quantityLive.value!!.quantity),
                    context,
                    isPropNameVisibleLive,
                    repo)
        }
        repo.firstQuantityValueLive.observeForever(firstQuantityObserver)
        repo.secondQuantityValueLive.observeForever(secondQuantityObserver)
    }

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            context.getString(R.string.preferenceKeyShowPropertyNames) ->
                (isPropNameVisibleLive as MutableLiveData).value = prefs.getBoolean(key, false)
            context.getString(R.string.preferenceKeyScientificFormatOnly) -> {
                CustomFormat.scientificFormatOnly = prefs.getBoolean(key, false)
                firstValue = firstValue
                secondValue = secondValue
                quantityModels.forEach { it.value = it.value }
            }
            context.getString(R.string.preferenceKeyDecimals) -> {
                CustomFormat.maxSymbols = prefs.getInt(key, 4)
                firstValue = firstValue
                secondValue = secondValue
                quantityModels.forEach { it.value = it.value }
            }
            context.getString(R.string.preferenceKeyUnitSet) -> {
                val unitSet = unitSetMap.mapKeys { context.getString(it.key) }.get(
                        prefs.getString(key, context.getString(R.string.unitSetDefaultValue)))
                if (unitSet != null){
                    computableProps.forEach {
                        repo.setEditUnit(it, unitSet[it.coherentUnit]!!)
                    }
                    allQuantities.forEach{
                        repo.setViewUnit(it, unitSet[it.coherentUnit]!!)
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

    private fun setFirstPropValue(value: Double) {
        repo.setProperties(firstQuantityValue.copy(value, firstUnitLive.value!!), secondQuantityValue)
    }

    private fun setSecondPropValue(value: Double) {
        repo.setProperties(firstQuantityValue, secondQuantityValue.copy(value, secondUnitLive.value!!))
    }

    fun inputFirstPropValue(input: CharSequence) {
        setFirstPropValue(inputValueToDouble(input))
    }

    fun inputSecondPropValue(input: CharSequence) {
        setSecondPropValue(inputValueToDouble(input))
    }

    fun selectFirstProp(index: Int) {
        nullIfNotInitialized { firstQuantityValue } ?: return
        val oldProp = firstQuantityValue.quantity
        val newProp = firstProps[index]
        val newSecondProps = COMPUTABLE_PROP_MAP[newProp]!!
        val oldSecondProp = nullIfNotInitialized { secondQuantityValue.quantity }
        val newSecondProp = when {
            newProp == oldSecondProp -> oldProp
            oldSecondProp in newSecondProps -> oldSecondProp
            oldProp in newSecondProps -> oldProp
            else -> newSecondProps.first()
        }!!
        repo.setProperties(repo.quantityValueLives[newProp]!!.value!!, repo.quantityValueLives[newSecondProp]!!.value!!)
    }

    fun selectSecondProp(index: Int) {
        val newType = secondProps[index]
        repo.setProperties(firstQuantityValue, repo.quantityValueLives[newType]!!.value!!)
    }

    fun selectFirstUnit(index: Int) {
        repo.setEditUnit(firstQuantityValue.quantity, firstUnits[index])
    }

    fun selectSecondUnit(index: Int) {
        repo.setEditUnit(secondQuantityValue.quantity, secondUnits[index])
    }
}