package ru.lextop.steamcalculator.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
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
    private val firstInputFocusLive: MutableLiveData<Boolean> = MutableLiveData()
    private val secondInputFocusLive: MutableLiveData<Boolean> = MutableLiveData()

    val quantityValueViewModels: List<QuantityValueViewModel>
    val refractiveIndexViewModel: RefractiveIndexViewModel
    private val firstQuantities: List<QuantityWrapper> = computablePropMap.keys.toList()
    private val firstQuantityNameLive: MutableLiveData<CharSequence> = MutableLiveData()
    private val secondQuantityNameLive: MutableLiveData<CharSequence> = MutableLiveData()
    private val firstQuantityNameToSymbolList: List<Pair<CharSequence, CharSequence>> = firstQuantities.map {
        context.getSpanned(it.symbolRes) to context.getSpanned(it.nameRes)
    }
    private var secondQuantities: List<QuantityWrapper> = listOf()
        set(value) {
            field = value
            secondQuantityNameToSymbolListLive.value = value.map {
                context.getSpanned(it.symbolRes) to context.getSpanned(it.nameRes)
            }
        }
    private val secondQuantityNameToSymbolListLive: MutableLiveData<List<Pair<CharSequence, CharSequence>>> = MutableLiveData()
    private val firstQuantitySelectionLive: MutableLiveData<Int> = MutableLiveData()
    private val secondQuantitySelectionLive: MutableLiveData<Int> = MutableLiveData()
    private val isQuantityNameVisibleLive: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var firstQuantityValue: QuantityValueWrapper
    private lateinit var secondQuantityValue: QuantityValueWrapper
    private var firstUnitLive: LiveData<UnitConverterWrapper> = MutableLiveData()
    private var secondUnitLive: LiveData<UnitConverterWrapper> = MutableLiveData()
    private val firstUnitsLive: MutableLiveData<List<CharSequence>> = MutableLiveData()
    private val secondUnitsLive: MutableLiveData<List<CharSequence>> = MutableLiveData()
    private var firstUnits: List<UnitConverterWrapper> = listOf()
        set(value) {
            field = value
            firstUnitsLive.value = value.map { context.getSpanned(it.symbolRes) }
        }
    private var secondUnits: List<UnitConverterWrapper> = listOf()
        set(value) {
            field = value
            secondUnitsLive.value = value.map { context.getSpanned(it.symbolRes) }
        }
    private val firstUnitSelectionLive: MutableLiveData<Int> = MutableLiveData()
    private val secondUnitSelectionLive: MutableLiveData<Int> = MutableLiveData()
    private val firstValueLive: MutableLiveData<CharSequence> = MutableLiveData()
    private val secondValueLive: MutableLiveData<CharSequence> = MutableLiveData()
    private fun setFirstValueLive(value: Double){
        firstValueLive.value = doubleToInputValue(value)
    }
    private fun setSecondValueLive(value: Double){
        secondValueLive.value = doubleToInputValue(value)
    }

    private val firstUnitObserver = Observer<UnitConverterWrapper> { unit ->
        firstUnitSelectionLive.value = firstUnits.indexOf(unit)
        setFirstValueLive(firstQuantityValue[unit!!].value)
        secondInputFocusLive.value = false
        firstInputFocusLive.value = true
    }
    private val secondUnitObserver = Observer<UnitConverterWrapper> { unit ->
        secondUnitSelectionLive.value = secondUnits.indexOf(unit)
        setSecondValueLive(secondQuantityValue[unit!!].value)
        if (secondUnitSelectedByUser) {
            firstInputFocusLive.value = false
            secondInputFocusLive.value = true
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
            firstQuantityNameLive.value = context.getSpanned(newQuantity.nameRes)
            firstQuantitySelectionLive.value = firstQuantities.indexOf(newQuantity)
            firstUnits = newQuantity.units
            firstUnitLive.removeObserver(firstUnitObserver)
            firstUnitLive = repo.getEditUnitLive(newQuantity)
            firstUnitLive.observeForever(firstUnitObserver)
            setFirstValueLive(quantityValue[firstUnitLive.value!!].value)
            secondQuantitySelectedByUser = false
            secondQuantities = computablePropMap[newQuantity]!!
        }
    }
    private val secondQuantityObserver = Observer<QuantityValueWrapper> { quantityValue ->
        val newQuantity = quantityValue!!.quantity
        val oldQuantity = nullIfNotInitialized { secondQuantityValue.quantity }
        secondQuantityValue = quantityValue
        secondQuantitySelectionLive.value = secondQuantities.indexOf(newQuantity)
        if (!secondQuantitySelectedByUser) {
            secondQuantitySelectedByUser = true
            secondUnitSelectedByUser = false
        }
        if (newQuantity != oldQuantity) {
            secondQuantityNameLive.value = context.getSpanned(newQuantity.nameRes)
            secondUnits = newQuantity.units
            secondUnitLive.removeObserver(secondUnitObserver)
            secondUnitLive = repo.getEditUnitLive(newQuantity)
            secondUnitLive.observeForever(secondUnitObserver)
            setSecondValueLive(quantityValue[secondUnitLive.value!!].value)
        }
    }

    init {
        isQuantityNameVisibleLive.value =
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
        refractiveIndexViewModel = RefractiveIndexViewModel(
                context = context,
                isQuantityNameVisibleLive = isQuantityNameVisibleLive,
                refractiveIndexQuantityValueViewModel = QuantityValueViewModel(
                repo.refractiveIndexQuantityValueLive,
                repo.getViewUnitLive(RefractiveIndex.wrapper),
                context,
                isQuantityNameVisibleLive,
                { repo.setViewUnit(RefractiveIndex.wrapper, it) }),
                wavelengthQuantityValueLive = repo.wavelengthQuantityValueLive,
                wavelengthUnitLive = repo.getEditUnitLive(Wavelength.wrapper),
                onWavelengthUnitSelected = { repo.setEditUnit(Wavelength.wrapper, it)},
                onWavelengthQuantityValueChanged = { repo.setWavelength(it) })
    }

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            context.getString(R.string.preferenceKeyShowPropertyNames) ->
                isQuantityNameVisibleLive.value = prefs.getBoolean(key, false)
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
                    (computableQuantities + Wavelength.wrapper).forEach {
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
        setFirstValueLive(inputValueToDouble(firstValueLive.value!!))
        setSecondValueLive(inputValueToDouble(secondValueLive.value!!))
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
        firstValueLive.value = input
        repo.setArg1Arg2(
                QuantityValueWrapper(
                        quantity = firstQuantityValue.quantity,
                        value = inputValueToDouble(input),
                        unit = firstUnitLive.value!!),
                secondQuantityValue)
    }

    fun inputSecondValue(input: CharSequence) {
        secondValueLive.value = input
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

    fun selectSecondQuantity(index: Int) {
        val newType = secondQuantities[index]
        repo.setArg1Arg2(firstQuantityValue, repo.quantityValueLives[newType]!!.value!!)
    }

    fun selectFirstUnit(index: Int) {
        repo.setEditUnit(firstQuantityValue.quantity, firstUnits[index])
    }

    fun selectSecondUnit(index: Int) {
        repo.setEditUnit(secondQuantityValue.quantity, secondUnits[index])
    }

    val firstSelectQuantityViewModel: SelectQuantityViewModel = object: SelectQuantityViewModel() {
        override val quantityNameToSymbolListLive: LiveData<List<Pair<CharSequence, CharSequence>>>
            = MutableLiveData<List<Pair<CharSequence, CharSequence>>>().apply{
                postValue(firstQuantityNameToSymbolList)
            }
        override val nameLive: LiveData<CharSequence>
            get() = firstQuantityNameLive
        override val nameVisibleLive: LiveData<Boolean>
            get() = isQuantityNameVisibleLive
        override val quantitySelectionLive: LiveData<Int>
            get() = firstQuantitySelectionLive

        override fun selectQuantity(position: Int) {
            selectFirstQuantity(position)
        }

        override val valueLive: LiveData<CharSequence>
            get() = firstValueLive

        override fun inputValue(value: CharSequence) {
            inputFirstValue(value)
        }

        override val focusLive: LiveData<Boolean>
            get() = firstInputFocusLive
        override val unitsLive: LiveData<List<CharSequence>>
            get() = firstUnitsLive
        override val unitSelectionLive: LiveData<Int>
            get() = firstUnitSelectionLive

        override fun selectUnit(position: Int) {
            selectFirstUnit(position)
        }
    }

    val secondSelectQuantityViewModel: SelectQuantityViewModel = object : SelectQuantityViewModel() {
        override val quantityNameToSymbolListLive: LiveData<List<Pair<CharSequence, CharSequence>>>
            get() = secondQuantityNameToSymbolListLive
        override val nameLive: LiveData<CharSequence>
            get() = secondQuantityNameLive
        override val nameVisibleLive: LiveData<Boolean>
            get() = isQuantityNameVisibleLive
        override val quantitySelectionLive: LiveData<Int>
            get() = secondQuantitySelectionLive

        override fun selectQuantity(position: Int) {
            selectSecondQuantity(position)
        }

        override val valueLive: LiveData<CharSequence>
            get() = secondValueLive

        override fun inputValue(value: CharSequence) {
            inputSecondValue(value)
        }

        override val focusLive: LiveData<Boolean>
            get() = secondInputFocusLive
        override val unitsLive: LiveData<List<CharSequence>>
            get() = secondUnitsLive
        override val unitSelectionLive: LiveData<Int>
            get() = secondUnitSelectionLive

        override fun selectUnit(position: Int) {
            selectSecondUnit(position)
        }
    }
}
