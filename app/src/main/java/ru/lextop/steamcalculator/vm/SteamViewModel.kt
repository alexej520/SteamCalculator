package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.SharedPreferences
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.binding.nullIfNotInitialized
import ru.lextop.steamcalculator.computablePropMap
import ru.lextop.steamcalculator.steam.quantity.UnitPh
import ru.lextop.steamcalculator.steam.quantity.Property
import ru.lextop.steamcalculator.steam.quantity.Quantity
import ru.lextop.steamcalculator.unitList
import javax.inject.Inject

class SteamViewModel @Inject constructor
(private val repo: SteamRepository, private val context: Context, private val prefs: SharedPreferences) : ViewModel() {
    val firstInputFocusLive: LiveData<Unit> = MutableLiveData()
    val secondInputFocusLive: LiveData<Unit> = MutableLiveData()

    val quantityModels: List<QuantityViewModel>
    private val firstProps: List<Property> = computablePropMap.keys.toList()
    val firstPropNameLive: LiveData<CharSequence> = MutableLiveData()
    val secondPropNameLive: LiveData<CharSequence> = MutableLiveData()
    val firstPropNameToSymbolList: List<Pair<CharSequence, CharSequence>> = firstProps.map {
        context.getSpanned(it.symbolId) to context.getSpanned(it.nameId)
    }
    private var secondProps: List<Property> = listOf()
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
    private lateinit var firstQuantity: Quantity
    private lateinit var secondQuantity: Quantity
    //set lateinit if Kotlin 1.2
    private var firstUnitLive: LiveData<UnitPh> = MutableLiveData()
    //set lateinit if Kotlin 1.2
    private var secondUnitLive: LiveData<UnitPh> = MutableLiveData()
    val firstUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    val secondUnitsLive: LiveData<List<CharSequence>> = MutableLiveData()
    private var firstUnits: List<UnitPh> = listOf()
        set(value) {
            field = value
            firstUnitsLive as MutableLiveData
            firstUnitsLive.value = value.map { context.getSpanned(it.id) }
        }
    private var secondUnits: List<UnitPh> = listOf()
        set(value) {
            field = value
            secondUnitsLive as MutableLiveData
            secondUnitsLive.value = value.map { context.getSpanned(it.id) }
        }
    val firstUnitSelectionLive: LiveData<Int> = MutableLiveData()
    val secondUnitSelectionLive: LiveData<Int> = MutableLiveData()
    val firstValueLive: LiveData<CharSequence> = MutableLiveData()
    val secondValueLive: LiveData<CharSequence> = MutableLiveData()

    private val firstUnitObserver = Observer<UnitPh> { unit ->
        (firstUnitSelectionLive as MutableLiveData).value = firstUnits.indexOf(unit)
        (firstValueLive as MutableLiveData).value = doubleToInputValue(firstQuantity[unit!!].value)
        (firstInputFocusLive as MutableLiveData).value = Unit
    }
    private val secondUnitObserver = Observer<UnitPh> { unit ->
        (secondUnitSelectionLive as MutableLiveData).value = secondUnits.indexOf(unit)
        (secondValueLive as MutableLiveData).value = doubleToInputValue(secondQuantity[unit!!].value)
        if (secondUnitSelectedByUser) {
            (secondInputFocusLive as MutableLiveData).value = Unit
        } else {
            secondUnitSelectedByUser = true
        }
    }
    private var secondPropSelectedByUser = false
    private var secondUnitSelectedByUser = false
    private val firstQuantityObserver = Observer<Quantity> { p ->
        val newProp = p!!.property
        val oldProp = nullIfNotInitialized { firstQuantity.property }
        firstQuantity = p
        if (newProp != oldProp) {
            (firstPropNameLive as MutableLiveData).value = context.getSpanned(newProp.nameId)
            (firstPropSelectionLive as MutableLiveData).value = firstProps.indexOf(newProp)
            firstUnits = newProp.unitList
            firstUnitLive.removeObserver(firstUnitObserver)
            firstUnitLive = repo.getEditUnitLive(newProp)
            firstUnitLive.observeForever(firstUnitObserver)
            (firstValueLive as MutableLiveData).value = doubleToInputValue(p[firstUnitLive.value!!].value)
            secondProps = computablePropMap[newProp]!!
            secondPropSelectedByUser = false
        }
    }
    private val secondQuantityObserver = Observer<Quantity> { p ->
        val newProp = p!!.property
        val oldProp = nullIfNotInitialized { secondQuantity.property }
        secondQuantity = p
        if (!secondPropSelectedByUser) {
            secondPropSelectedByUser = true
            (secondPropSelectionLive as MutableLiveData).value = secondProps.indexOf(newProp)
            secondUnitSelectedByUser = false
        }
        if (newProp != oldProp) {
            (secondPropNameLive as MutableLiveData).value = context.getSpanned(newProp.nameId)
            secondUnits = newProp.unitList
            secondUnitLive.removeObserver(secondUnitObserver)
            secondUnitLive = repo.getEditUnitLive(newProp)
            secondUnitLive.observeForever(secondUnitObserver)
            (secondValueLive as MutableLiveData).value = doubleToInputValue(p[secondUnitLive.value!!].value)
        }
    }

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            "showPropNames" -> (isPropNameVisibleLive as MutableLiveData).value = prefs.getBoolean(key, false)
        }
    }

    init {
        (isPropNameVisibleLive as MutableLiveData).value = prefs.getBoolean("showPropNames", false)
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
        quantityModels = repo.quantityLives.values.map { quantityLive ->
            QuantityViewModel(
                    quantityLive,
                    repo.getViewUnitLive(quantityLive.value!!.property),
                    context,
                    isPropNameVisibleLive,
                    repo)
        }
        repo.firstQuantityLive.observeForever(firstQuantityObserver)
        repo.secondQuantityLive.observeForever(secondQuantityObserver)
    }

    override fun onCleared() {
        repo.firstQuantityLive.removeObserver(firstQuantityObserver)
        repo.secondQuantityLive.removeObserver(secondQuantityObserver)
        firstUnitLive.removeObserver(firstUnitObserver)
        secondUnitLive.removeObserver(secondUnitObserver)
    }

    private fun doubleToInputValue(double: Double): CharSequence =
            CustomFormat.formatIgnoreNaN(double)

    private fun inputValueToDouble(input: CharSequence): Double =
            CustomFormat.parse(input.toString())

    private fun setFirstPropValue(value: Double) {
        repo.setProperties(firstQuantity.copy(value, firstUnitLive.value!!), secondQuantity)
    }

    private fun setSecondPropValue(value: Double) {
        repo.setProperties(firstQuantity, secondQuantity.copy(value, secondUnitLive.value!!))
    }

    fun inputFirstPropValue(input: CharSequence) {
        setFirstPropValue(inputValueToDouble(input))
    }

    fun inputSecondPropValue(input: CharSequence) {
        setSecondPropValue(inputValueToDouble(input))
    }

    fun selectFirstProp(index: Int) {
        nullIfNotInitialized { firstQuantity } ?: return
        val oldProp = firstQuantity.property
        val newProp = firstProps[index]
        val newSecondProps = computablePropMap[newProp]!!
        val oldSecondProp = nullIfNotInitialized { secondQuantity.property }
        val newSecondProp = when {
            newProp == oldSecondProp -> oldProp
            oldSecondProp in newSecondProps -> oldSecondProp
            oldProp in newSecondProps -> oldProp
            else -> newSecondProps.first()
        }!!
        repo.setProperties(repo.quantityLives[newProp]!!.value!!, repo.quantityLives[newSecondProp]!!.value!!)
    }

    fun selectSecondProp(index: Int) {
        val newType = secondProps[index]
        repo.setProperties(firstQuantity, repo.quantityLives[newType]!!.value!!)
    }

    fun selectFirstUnit(index: Int) {
        repo.setEditUnit(firstQuantity.property, firstUnits[index])
    }

    fun selectSecondUnit(index: Int) {
        repo.setEditUnit(secondQuantity.property, secondUnits[index])
    }
}