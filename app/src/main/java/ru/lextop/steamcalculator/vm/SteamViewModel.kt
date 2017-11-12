package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Context
import ru.lextop.steamcalculator.SteamRepository
import ru.lextop.steamcalculator.binding.nullIfNotInitialized
import ru.lextop.steamcalculator.computablePropMap
import ru.lextop.steamcalculator.steam.quantity.UnitPh
import ru.lextop.steamcalculator.steam.quantity.Property
import ru.lextop.steamcalculator.steam.quantity.Quantity
import ru.lextop.steamcalculator.unitList
import javax.inject.Inject

class SteamViewModel @Inject constructor
(private val repo: SteamRepository, private val context: Context) : ViewModel() {
    val firstInputFocusLive: LiveData<Unit> = MutableLiveData()
    val secondInputFocusLive: LiveData<Unit> = MutableLiveData()

    val quantityModels: List<QuantityViewModel>
    val firstProps: List<Property> = computablePropMap.keys.toList()
    val secondPropsLive: LiveData<List<Property>> = MutableLiveData()
    val firstPropSelectionLive: LiveData<Int> = MutableLiveData()
    val secondPropSelectionLive: LiveData<Int> = MutableLiveData()

    private lateinit var firstQuantity: Quantity
    private lateinit var secondQuantity: Quantity
    //set lateinit if Kotlin 1.2
    private var firstUnitLive: LiveData<UnitPh> = MutableLiveData()
    //set lateinit if Kotlin 1.2
    private var secondUnitLive: LiveData<UnitPh> = MutableLiveData()
    val firstUnitsLive: LiveData<List<String>> = MutableLiveData()
    val secondUnitsLive: LiveData<List<String>> = MutableLiveData()
    private var firstUnits: List<UnitPh> = listOf()
        set(value) {
            field = value
            firstUnitsLive as MutableLiveData
            firstUnitsLive.value = value.map { context.getString(it.id) }
        }
    private var secondUnits: List<UnitPh> = listOf()
        set(value) {
            field = value
            secondUnitsLive as MutableLiveData
            secondUnitsLive.value = value.map { context.getString(it.id) }
        }
    val firstUnitSelectionLive: LiveData<Int> = MutableLiveData()
    val secondUnitSelectionLive: LiveData<Int> = MutableLiveData()
    val firstValueLive: LiveData<CharSequence> = MutableLiveData()
    val secondValueLive: LiveData<CharSequence> = MutableLiveData()

    private val firstUnitObserver = Observer<UnitPh> { unit ->
        (firstUnitSelectionLive as MutableLiveData).value = firstUnits.indexOf(unit)
        firstUnitSelectionLive.value = firstUnits.indexOf(unit)
    }
    private val secondUnitObserver = Observer<UnitPh> { unit ->
        (secondUnitSelectionLive as MutableLiveData).value = secondUnits.indexOf(unit)
        secondUnitSelectionLive.value = secondUnits.indexOf(unit)
    }
    private val firstQuantityObserver = Observer<Quantity> { p ->
        val newProp = p!!.property
        val oldProp = nullIfNotInitialized { firstQuantity.property }
        firstQuantity = p
        if (newProp != oldProp) {
            firstPropSelectionLive as MutableLiveData
            firstPropSelectionLive.value = firstProps.indexOf(newProp)
            firstUnits = newProp.unitList
            firstUnitLive.removeObserver(firstUnitObserver)
            firstUnitLive = repo.getEditUnitLive(newProp)
            firstUnitLive.observeForever(firstUnitObserver)
            secondPropsLive as MutableLiveData
            secondPropsLive.value = computablePropMap[newProp]
        }
    }
    private val secondPropObserver = Observer<Quantity> { p ->
        val newProp = p!!.property
        val oldProp = nullIfNotInitialized { secondQuantity.property }
        secondQuantity = p
        if (newProp != oldProp) {
            secondPropSelectionLive as MutableLiveData
            secondPropSelectionLive.value = secondPropsLive.value!!.indexOf(newProp)
            secondUnits = newProp.unitList
            secondUnitLive.removeObserver(secondUnitObserver)
            secondUnitLive = repo.getEditUnitLive(newProp)
            secondUnitLive.observeForever(secondUnitObserver)
        }
    }

    init {
        quantityModels = repo.quantityLives.values.map { quantityLive ->
            QuantityViewModel(
                    quantityLive,
                    repo.getViewUnitLive(quantityLive.value!!.property),
                    context,
                    repo)
        }
        (quantityModels as MutableList).addAll(quantityModels)
        quantityModels.addAll(quantityModels)
        quantityModels.addAll(quantityModels)
        repo.firstQuantityLive.observeForever(firstQuantityObserver)
        repo.secondQuantityLive.observeForever(secondPropObserver)
    }

    override fun onCleared() {
        repo.firstQuantityLive.removeObserver(firstQuantityObserver)
        repo.secondQuantityLive.removeObserver(secondPropObserver)
        firstUnitLive.removeObserver(firstUnitObserver)
        secondUnitLive.removeObserver(secondUnitObserver)
    }

    private fun inputValueToDouble(input: CharSequence): Double =
            input.toString().toDoubleOrNull() ?: Double.NaN

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
        val newType = secondPropsLive.value!![index]
        repo.setProperties(firstQuantity, repo.quantityLives[newType]!!.value!!)
    }

    fun selectFirstUnit(index: Int) {
        repo.setEditUnit(firstQuantity.property, firstUnits[index])
    }

    fun selectSecondUnit(index: Int) {
        repo.setEditUnit(secondQuantity.property, secondUnits[index])
    }
}