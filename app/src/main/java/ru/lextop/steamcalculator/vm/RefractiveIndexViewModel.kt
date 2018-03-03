package ru.lextop.steamcalculator.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.binding.nullIfNotInitialized
import ru.lextop.steamcalculator.model.QuantityValueWrapper
import ru.lextop.steamcalculator.model.UnitConverterWrapper
import ru.lextop.steamcalculator.model.wrapper
import steam.quantities.Wavelength

class RefractiveIndexViewModel(
        context: Context,
        val wavelengthQuantityValueLive: LiveData<QuantityValueWrapper>,
        val wavelengthUnitLive: LiveData<UnitConverterWrapper>,
        val isQuantityNameVisibleLive: LiveData<Boolean>,
        val refractiveIndexQuantityValueViewModel: QuantityValueViewModel,
        val onWavelengthUnitSelected: (UnitConverterWrapper) -> Unit,
        val onWavelengthQuantityValueChanged: (QuantityValueWrapper) -> Unit
) {
    val WavelengthWrapper = Wavelength.wrapper

    val wavelengthUnits: List<CharSequence> = WavelengthWrapper.units.map { context.getSpanned(it.symbolRes) }
    val wavelengthUnitSelectionLive: LiveData<Int> = MutableLiveData()
    val wavelengthValueLive: LiveData<CharSequence> = MutableLiveData()
    private lateinit var wavelengthQuantityValue: QuantityValueWrapper
    private fun setWavelengthValueLive(value: Double){
        (wavelengthValueLive as MutableLiveData).value = doubleToInputValue(value)
    }

    private val waveLengthQuantityValueObserver = Observer<QuantityValueWrapper> { quantityValue ->
        val oldQuantity = nullIfNotInitialized { wavelengthQuantityValue }
        wavelengthQuantityValue = quantityValue!!
        if (oldQuantity == null){
            wavelengthUnitLive.observeForever(wavelengthUnitObserver)
        }
    }

    private val wavelengthUnitObserver = Observer<UnitConverterWrapper> { unit ->
        (wavelengthUnitSelectionLive as MutableLiveData).value = WavelengthWrapper.units.indexOf(unit)
        setWavelengthValueLive(wavelengthQuantityValue[unit!!].value)
    }

    init {
        wavelengthQuantityValueLive.observeForever(waveLengthQuantityValueObserver)
    }


    private fun doubleToInputValue(double: Double): CharSequence =
            CustomFormat.formatIgnoreNaN(double)

    private fun inputValueToDouble(input: CharSequence): Double =
            CustomFormat.parse(input.toString())

    fun onCleared() {
        wavelengthQuantityValueLive.removeObserver(waveLengthQuantityValueObserver)
        wavelengthUnitLive.removeObserver(wavelengthUnitObserver)
    }

    fun updateValue() {
        setWavelengthValueLive(inputValueToDouble(wavelengthValueLive.value!!))
        refractiveIndexQuantityValueViewModel.updateValue()
    }

    fun selectWavelengthUnit(index: Int) {
        onWavelengthUnitSelected(WavelengthWrapper.units[index])
    }

    fun inputWavelengthValue(input: CharSequence) {
        (wavelengthValueLive as MutableLiveData).value = input
        onWavelengthQuantityValueChanged(QuantityValueWrapper(
                quantity = WavelengthWrapper,
                value = inputValueToDouble(input),
                unit = WavelengthWrapper.units[wavelengthUnitSelectionLive.value!!]))
    }
}