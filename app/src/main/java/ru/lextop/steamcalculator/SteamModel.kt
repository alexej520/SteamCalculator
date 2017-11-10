package ru.lextop.steamcalculator

import ru.lextop.steamcalculator.steam.quantity.*

val Property.unitList get() = baseUnit.derivativeUnitMap.values.toList()

val init = listOf(PressureUnit, TemperatureUnit, SpecificEnergyUnit, RatioUnit)

val resPropMap = mapOf<Property, Int>(
        Pressure to R.string.pressure,
        Temperature to R.string.temperature,
        SpecificEnthalpy to R.string.enthalpy,
        SpecificEntropy to R.string.entropy,
        VapourFraction to R.string.vapourFraction
).mapKeys { it.key }
val resBaseUnitMap = mapOf<BaseUnit, Int>(
        Pressure.baseUnit to R.string.pressureUnits,
        Temperature.baseUnit to R.string.temperatureUnits,
        SpecificEnthalpy.baseUnit to R.string.specificEnergyUnits,
        VapourFraction.baseUnit to R.string.ratioUnits
)
private val computablePairs: List<Pair<Property, Property>> = listOf(
        Pressure to Temperature,
        SpecificEnthalpy to SpecificEntropy,
        Pressure to SpecificEnthalpy,
        Pressure to VapourFraction,
        Temperature to VapourFraction,
        Pressure to SpecificEntropy,
        Temperature to SpecificEntropy
)

val props = resPropMap.keys.toList()

val computablePropMap: Map<Property, List<Property>> = props.associate { type ->
    val symbol = type.symbol
    type to computablePairs.mapNotNull { (p1, p2) ->
        when (symbol) {
            p1.symbol -> p2
            p2.symbol -> p1
            else -> null
        }
    }
}.filter { it.value.isNotEmpty() }

fun String.toProperty(): Property =
        props.first { this == it.symbol }

fun String.toUnit(propType: Property): DerivativeUnit =
        propType.baseUnit.derivativeUnitMap[this]!!