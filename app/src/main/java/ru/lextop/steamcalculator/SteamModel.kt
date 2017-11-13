package ru.lextop.steamcalculator

import ru.lextop.steamcalculator.steam.quantity.*

val Property.unitList get() = baseUnit.unitList

val units = Units

private val computablePairs: List<Pair<Property, Property>> = listOf(
        Pressure to Temperature,
        Pressure to SpecificEnthalpy,
        Pressure to SpecificEntropy,
        SpecificEnthalpy to SpecificEntropy,
        Temperature to SpecificEntropy,
        Density to Temperature,
        SpecificVolume to Temperature,
        Pressure to VapourFraction,
        Temperature to VapourFraction
)

val computablePropMap: Map<Property, List<Property>> = with(computablePairs.unzip()){
    first.union(second).associate { prop ->
        prop to computablePairs.mapNotNull { (p1, p2) ->
            when (prop) {
                p1 -> p2
                p2 -> p1
                else -> null
            }
        }
    }
}

val computableProps = computablePropMap.keys.toList()

val allProps = Property.symbolPropMap.values

fun String.toProperty(): Property =
        Property.symbolPropMap[this]!!

fun String.toUnit(prop: Property): UnitPh =
        prop.baseUnit.unitMap[this]!!