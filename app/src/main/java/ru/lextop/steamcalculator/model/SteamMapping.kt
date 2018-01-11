package ru.lextop.steamcalculator.model

import quantityvalue.Dimension
import quantityvalue.Quantity
import quantityvalue.UnitPh
import ru.lextop.steamcalculator.R
import steam.quantities.*
import steam.units.*

private val computablePairs: List<Pair<Quantity, Quantity>> = listOf(
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

val computablePropMap: Map<Quantity, List<Quantity>> = with(computablePairs.unzip()) {
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

val computableQuantities = computablePropMap.keys.toList()

val quantitiesMapping: List<Triple<Quantity, Int, Int>> = listOf(
        Triple(Pressure, R.string.Pressure, R.string.P),
        Triple(Temperature, R.string.Temperature, R.string.T),
        Triple(SpecificEnthalpy, R.string.SpecificEnthalpy, R.string.h),
        Triple(SpecificEntropy, R.string.SpecificEntropy, R.string.s),
        Triple(VapourFraction, R.string.VapourFraction, R.string.x),
        Triple(SpecificVolume, R.string.SpecificVolume, R.string.v),
        Triple(Density, R.string.Density, R.string.rho),
        Triple(SpeedOfSound, R.string.SpeedOfSound, R.string.w),
        Triple(SpecificIsobaricHeatCapacity, R.string.SpecificIsobaricHeatCapacity, R.string.cp),
        Triple(SpecificIsochoricHeatCapacity, R.string.SpecificIsochoricHeatCapacity, R.string.cv),
        Triple(SpecificEnthalpyOfVaporization, R.string.SpecificEnthalpyOfVaporization, R.string.hvap),
        Triple(ThermalConductivity, R.string.ThermalConductivity, R.string.lambda),
        Triple(ThermalDiffusivity, R.string.ThermalDiffusivity, R.string.k),
        Triple(PrandtlNumber, R.string.PrandtlNumber, R.string.Pr),
        Triple(DynamicViscosity, R.string.DynamicViscosity, R.string.eta),
        Triple(KinematicViscosity, R.string.KinematicViscosity, R.string.nu),
        Triple(SurfaceTension, R.string.SurfaceTension, R.string.sigma),
        Triple(IsobaricCubicExpansionCoefficient, R.string.IsobaricCubicExpansionCoefficient, R.string.av),
        Triple(IsothermalCompressibility, R.string.IsothermalCompressibility, R.string.kT),
        Triple(RelativePermittivity, R.string.RelativePermittivity, R.string.epsilon),
        Triple(SpecificInternalEnergy, R.string.SpecificInternalEnergy, R.string.u),
        Triple(SpecificGibbsFreeEnergy, R.string.SpecificGibbsFreeEnergy, R.string.g)
)

private val Quantity_nameId = quantitiesMapping.associate { it.first to it.second }
private val Quantity_symbolId = quantitiesMapping.associate { it.first to it.third }

val Quantity.nameId: Int get() = Quantity_nameId[this]!!
val Quantity.symbolId: Int get() = Quantity_symbolId[this]!!

val allQuantities: List<Quantity> = quantitiesMapping.map { it.first }

private val String_toQuantity = allQuantities.associate { it.symbol to it }

fun String.toQuantity(): Quantity = String_toQuantity[this]!!

private val unitsMapping: List<Pair<UnitPh, Int>> = listOf(

// Mass

        Pair(kg, R.string.kg),
        Pair(lb, R.string.lb),

// Energy

        Pair(J, R.string.J),
        Pair(kJ, R.string.kJ),
        Pair(erg, R.string.erg),
        Pair(cal, R.string.cal),
        Pair(calth, R.string.calth),
        Pair(cal15, R.string.cal15),
        Pair(kcal, R.string.kcal),
        Pair(kcalth, R.string.kcalth),
        Pair(kcal15, R.string.kcal15),
        Pair(BTU, R.string.BTU),
        Pair(kWh, R.string.kWh),

// Specific Energy

        Pair(J_kg, R.string.J_kg),
        Pair(kJ_kg, R.string.kJ_kg),
        Pair(cal_kg, R.string.cal_kg),
        Pair(calth_kg, R.string.calth_kg),
        Pair(cal15_kg, R.string.cal15_kg),
        Pair(kcal_kg, R.string.kcal_kg),
        Pair(kcalth_kg, R.string.kcalth_kg),
        Pair(kcal15_kg, R.string.kcal15_kg),
        Pair(BTU_lb, R.string.BTU_lb),

// Temperature

        Pair(K, R.string.K),
        Pair(C, R.string.C),
        Pair(F, R.string.F),
        Pair(Ra, R.string.Ra),
        Pair(D, R.string.D),
        Pair(N, R.string.N),
        Pair(Re, R.string.Re),
        Pair(Ro, R.string.Ro),

// Specific Heat Capacity

        Pair(J_kgK, R.string.J_kgK),
        Pair(kJ_kgK, R.string.kJ_kgK),
        Pair(cal_kgK, R.string.cal_kgK),
        Pair(calth_kgK, R.string.calth_kgK),
        Pair(cal15_kgK, R.string.cal15_kgK),
        Pair(kcal_kgK, R.string.kcal_kgK),
        Pair(kcalth_kgK, R.string.kcalth_kgK),
        Pair(kcal15_kgK, R.string.kcal15_kgK),
        Pair(BTU_lbR, R.string.BTU_lbR),

// Pressure

        Pair(Pa, R.string.Pa),
        Pair(kPa, R.string.kPa),
        Pair(MPa, R.string.MPa),
        Pair(bar, R.string.bar),
        Pair(at, R.string.at),
        Pair(kgf_cm2, R.string.kgf_cm2),
        Pair(atm, R.string.atm),
        Pair(psi, R.string.psi),
        Pair(lb_in2, R.string.lb_in2),
        Pair(mmHg, R.string.mmHg),
        Pair(mH2O, R.string.mH2O),

// Ratio

        Pair(ratio, R.string.ratio),
        Pair(percent, R.string.percent),

// Specific Volume

        Pair(m3_kg, R.string.m3_kg),
        Pair(ft3_lb, R.string.ft3_lb),

// Density

        Pair(kg_m3, R.string.kg_m3),
        Pair(lb_ft3, R.string.lb_ft3),

// Dynamic Viscosity

        Pair(Pas, R.string.Pas),
        Pair(cP, R.string.cP),

// Temperature^-1

        Pair(K_1, R.string.K_1),
        Pair(R_1, R.string.R_1),

// Compressibility

        Pair(Pa_1, R.string.Pa_1),
        Pair(MPa_1, R.string.MPa_1),
        Pair(in2_lb, R.string.in2_lb),

// Kinematic Viscosity

        Pair(m2_s, R.string.m2_s),
        Pair(cSt, R.string.cSt),

// Speed

        Pair(m_s, R.string.m_s),
        Pair(ft_s, R.string.ft_s),

// Surface Tension

        Pair(N_m, R.string.N_m),
        Pair(kg_s2, R.string.kg_s2),
        Pair(lbf_ft, R.string.lbf_ft),

// Thermal Conductivity

        Pair(W_mK, R.string.W_mK),
        Pair(kW_mK, R.string.kW_mK),
        Pair(BTU_hrftR, R.string.BTU_hrftR)
)

private val UnitPh_symbolId = unitsMapping.associate { it.first to it.second }
val UnitPh.symbolId: Int get() = UnitPh_symbolId[this]!!

private val Dimension_unitList = unitsMapping.map { it.first }.groupBy { it.dimension }

val Dimension.unitList: List<UnitPh> get() = Dimension_unitList[this]!!

private val String_toUnit = unitsMapping.associate { it.first.symbol to it.first }

fun String.toUnit(): UnitPh = String_toUnit[this]!!

val defaultUnits = listOf(
        kg_m3, ratio, Pas, K_1, MPa_1, m2_s, MPa, kJ_kg, kJ_kgK, m3_kg, m_s, N_m, K, W_mK
).associate { it.dimension to it }

val engineeringUnits = listOf(
        kg_m3, ratio, Pas, K_1, MPa_1, m2_s, bar, kJ_kg, kJ_kgK, m3_kg, m_s, N_m, C, kW_mK
).associate { it.dimension to it }

val siUnits = listOf(
        kg_m3, ratio, Pas, K_1, Pa_1, m2_s, Pa, J_kg, J_kgK, m3_kg, m_s, N_m, K, W_mK
).associate { it.dimension to it }

val imperialUnits = listOf(
        lb_ft3, ratio, cP, R_1, in2_lb, cSt, psi, BTU_lb, BTU_lbR, ft3_lb, ft_s, lbf_ft, F, BTU_hrftR
).associate { it.dimension to it }

val unitSetMap = mapOf(
        R.string.unitSetDefaultValue to defaultUnits,
        R.string.unitSetEngineeringValue to engineeringUnits,
        R.string.unitSetSIValue to siUnits,
        R.string.unitSetImperialValue to imperialUnits
)
