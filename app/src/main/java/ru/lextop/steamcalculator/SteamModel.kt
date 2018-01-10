package ru.lextop.steamcalculator

import ru.lextop.steamcalculator.steam.quantity.*
import ru.lextop.steamcalculator.steam.quantity.Units.Compressibility.MPa_1
import ru.lextop.steamcalculator.steam.quantity.Units.Compressibility.Pa_1
import ru.lextop.steamcalculator.steam.quantity.Units.Compressibility.in2_lb
import ru.lextop.steamcalculator.steam.quantity.Units.Density.kg_m3
import ru.lextop.steamcalculator.steam.quantity.Units.Density.lb_ft3
import ru.lextop.steamcalculator.steam.quantity.Units.DynamicViscosity.Pas
import ru.lextop.steamcalculator.steam.quantity.Units.DynamicViscosity.cP
import ru.lextop.steamcalculator.steam.quantity.Units.KinematicViscosity.cSt
import ru.lextop.steamcalculator.steam.quantity.Units.KinematicViscosity.m2_s
import ru.lextop.steamcalculator.steam.quantity.Units.Pressure.MPa
import ru.lextop.steamcalculator.steam.quantity.Units.Pressure.Pa
import ru.lextop.steamcalculator.steam.quantity.Units.Pressure.bar
import ru.lextop.steamcalculator.steam.quantity.Units.Pressure.psi
import ru.lextop.steamcalculator.steam.quantity.Units.Ratio.ratio
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.BTU_lb
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.J_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.kJ_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificHeatCapacity.BTU_lbR
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificHeatCapacity.J_kgK
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificHeatCapacity.kJ_kgK
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificVolume.ft3_lb
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificVolume.m3_kg
import ru.lextop.steamcalculator.steam.quantity.Units.Speed.ft_s
import ru.lextop.steamcalculator.steam.quantity.Units.Speed.m_s
import ru.lextop.steamcalculator.steam.quantity.Units.SurfaceTension.N_m
import ru.lextop.steamcalculator.steam.quantity.Units.SurfaceTension.lbf_ft
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature.C
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature.F
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature.K
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature_1.K_1
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature_1.R_1
import ru.lextop.steamcalculator.steam.quantity.Units.ThermalConductivity.BTU_hrftR
import ru.lextop.steamcalculator.steam.quantity.Units.ThermalConductivity.W_mK
import ru.lextop.steamcalculator.steam.quantity.Units.ThermalConductivity.kW_mK
import ru.lextop.steamcalculator.steam.quantity.DerivedUnit

val Quantity.unitList get() = coherentUnit.derivedUnits

val units = Units

private val COMPUTABLE_PAIRS: List<Pair<Quantity, Quantity>> = listOf(
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

val COMPUTABLE_PROP_MAP: Map<Quantity, List<Quantity>> = with(COMPUTABLE_PAIRS.unzip()){
    first.union(second).associate { prop ->
        prop to COMPUTABLE_PAIRS.mapNotNull { (p1, p2) ->
            when (prop) {
                p1 -> p2
                p2 -> p1
                else -> null
            }
        }
    }
}

val computableProps = COMPUTABLE_PROP_MAP.keys.toList()

val allQuantities = Quantity.QUANTITY_MAP.values

fun String.toQuantity(): Quantity =
        Quantity.QUANTITY_MAP[this]!!

fun String.toUnit(prop: DerivedQuantity): DerivedUnit =
        prop.coherentUnit.derivedUnitMap[this]!!

val defaultUnits = listOf(
        kg_m3, ratio, Pas, K_1, MPa_1, m2_s, MPa, kJ_kg, kJ_kgK, m3_kg, m_s, N_m, K, W_mK
).associate { it.coherentUnit to it }

val engineeringUnits = listOf(
        kg_m3, ratio, Pas, K_1, MPa_1, m2_s, bar, kJ_kg, kJ_kgK, m3_kg, m_s, N_m, C, kW_mK
).associate { it.coherentUnit to it }

val siUnits = listOf(
        kg_m3, ratio, Pas, K_1, Pa_1, m2_s, Pa, J_kg, J_kgK, m3_kg, m_s, N_m, K, W_mK
).associate { it.coherentUnit to it }

val imperialUnits = listOf(
        lb_ft3, ratio, cP, R_1, in2_lb, cSt, psi, BTU_lb, BTU_lbR, ft3_lb, ft_s, lbf_ft, F, BTU_hrftR
).associate { it.coherentUnit to it }

val unitSetMap = mapOf(
         R.string.unitSetDefaultValue to defaultUnits,
        R.string.unitSetEngineeringValue to engineeringUnits,
        R.string.unitSetSIValue to siUnits,
        R.string.unitSetImperialValue to imperialUnits
)