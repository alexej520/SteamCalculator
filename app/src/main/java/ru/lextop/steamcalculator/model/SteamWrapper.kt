package ru.lextop.steamcalculator.model

import quantityvalue.Quantity
import quantityvalue.QuantityValue
import quantityvalue.UnitConverter
import quantityvalue.invoke
import ru.lextop.steamcalculator.R
import steam.Steam
import steam.quantities.*
import steam.units.*

class UnitConverterWrapper(
        val id: Int,
        val unit: UnitConverter,
        val symbolRes: Int
)

private val allUnitMappings = mutableListOf<UnitConverterWrapper>()

private val _massUnits = listOf(
        UnitConverterWrapper(1, kg, R.string.kg),
        UnitConverterWrapper(2, lb, R.string.lb)
).also { allUnitMappings.addAll(it) }

private val _energyUnits = listOf(
        UnitConverterWrapper(3, J, R.string.J),
        UnitConverterWrapper(4, kJ, R.string.kJ),
        UnitConverterWrapper(5, erg, R.string.erg),
        UnitConverterWrapper(6, cal, R.string.cal),
        UnitConverterWrapper(7, calth, R.string.calth),
        UnitConverterWrapper(8, cal15, R.string.cal15),
        UnitConverterWrapper(9, kcal, R.string.kcal),
        UnitConverterWrapper(10, kcalth, R.string.kcalth),
        UnitConverterWrapper(11, kcal15, R.string.kcal15),
        UnitConverterWrapper(12, BTU, R.string.BTU),
        UnitConverterWrapper(13, kWh, R.string.kWh)
).also { allUnitMappings.addAll(it) }

private val _specificEnergyUnits = listOf(
        UnitConverterWrapper(14, J_kg, R.string.J_kg),
        UnitConverterWrapper(15, kJ_kg, R.string.kJ_kg),
        UnitConverterWrapper(16, cal_kg, R.string.cal_kg),
        UnitConverterWrapper(17, calth_kg, R.string.calth_kg),
        UnitConverterWrapper(18, cal15_kg, R.string.cal15_kg),
        UnitConverterWrapper(19, kcal_kg, R.string.kcal_kg),
        UnitConverterWrapper(20, kcalth_kg, R.string.kcalth_kg),
        UnitConverterWrapper(21, kcal15_kg, R.string.kcal15_kg),
        UnitConverterWrapper(22, BTU_lb, R.string.BTU_lb)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = kJ_kg, engineering = kJ_kg, si = J_kg, imperial = BTU_lb)

private val _temperatureUnits = listOf(
        UnitConverterWrapper(23, K, R.string.K),
        UnitConverterWrapper(24, C, R.string.C),
        UnitConverterWrapper(25, F, R.string.F),
        UnitConverterWrapper(26, Ra, R.string.Ra),
        UnitConverterWrapper(27, D, R.string.D),
        UnitConverterWrapper(28, N, R.string.N),
        UnitConverterWrapper(29, Re, R.string.Re),
        UnitConverterWrapper(30, Ro, R.string.Ro)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = K, engineering = C, si = K, imperial = F)

private val _specificHeatCapacityUnits = listOf(
        UnitConverterWrapper(31, J_kgK, R.string.J_kgK),
        UnitConverterWrapper(32, kJ_kgK, R.string.kJ_kgK),
        UnitConverterWrapper(33, cal_kgK, R.string.cal_kgK),
        UnitConverterWrapper(34, calth_kgK, R.string.calth_kgK),
        UnitConverterWrapper(35, cal15_kgK, R.string.cal15_kgK),
        UnitConverterWrapper(36, kcal_kgK, R.string.kcal_kgK),
        UnitConverterWrapper(37, kcalth_kgK, R.string.kcalth_kgK),
        UnitConverterWrapper(38, kcal15_kgK, R.string.kcal15_kgK),
        UnitConverterWrapper(39, BTU_lbR, R.string.BTU_lbR)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = kJ_kgK, engineering = kJ_kgK, si = J_kgK, imperial = BTU_lbR)

private val _pressureUnits = listOf(
        UnitConverterWrapper(40, Pa, R.string.Pa),
        UnitConverterWrapper(41, kPa, R.string.kPa),
        UnitConverterWrapper(42, MPa, R.string.MPa),
        UnitConverterWrapper(43, bar, R.string.bar),
        UnitConverterWrapper(44, at, R.string.at),
        UnitConverterWrapper(45, kgf_cm2, R.string.kgf_cm2),
        UnitConverterWrapper(46, atm, R.string.atm),
        UnitConverterWrapper(47, psi, R.string.psi),
        UnitConverterWrapper(48, lb_in2, R.string.lb_in2),
        UnitConverterWrapper(49, mmHg, R.string.mmHg),
        UnitConverterWrapper(50, mH2O, R.string.mH2O)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = MPa, engineering = bar, si = Pa, imperial = psi)

private val _ratioUnits = listOf(
        UnitConverterWrapper(51, ratio, R.string.ratio),
        UnitConverterWrapper(52, percent, R.string.percent)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = ratio, engineering = ratio, si = ratio, imperial = ratio)

private val _specificVolumeUnits = listOf(
        UnitConverterWrapper(53, m3_kg, R.string.m3_kg),
        UnitConverterWrapper(54, ft3_lb, R.string.ft3_lb)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = m3_kg, engineering = m3_kg, si = m3_kg, imperial = ft3_lb)

private val _densityUnits = listOf(
        UnitConverterWrapper(55, kg_m3, R.string.kg_m3),
        UnitConverterWrapper(56, lb_ft3, R.string.lb_ft3)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = kg_m3, engineering = kg_m3, si = kg_m3, imperial = lb_ft3)

private val _dynamicViscosityUnits = listOf(
        UnitConverterWrapper(57, Pas, R.string.Pas),
        UnitConverterWrapper(58, cP, R.string.cP)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = Pas, engineering = Pas, si = Pas, imperial = cP)

private val _temperature_1Units = listOf(
        UnitConverterWrapper(59, K_1, R.string.K_1),
        UnitConverterWrapper(60, R_1, R.string.R_1)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = K_1, engineering = K_1, si = K_1, imperial = R_1)

private val _compressibilityUnits = listOf(
        UnitConverterWrapper(61, Pa_1, R.string.Pa_1),
        UnitConverterWrapper(62, MPa_1, R.string.MPa_1),
        UnitConverterWrapper(63, in2_lb, R.string.in2_lb)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = MPa_1, engineering = MPa_1, si = Pa_1, imperial = in2_lb)

private val _kinematicViscosityUnits = listOf(
        UnitConverterWrapper(64, m2_s, R.string.m2_s),
        UnitConverterWrapper(65, cSt, R.string.cSt)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = m2_s, engineering = m2_s, si = m2_s, imperial = cSt)

private val _speedUnits = listOf(
        UnitConverterWrapper(66, m_s, R.string.m_s),
        UnitConverterWrapper(67, ft_s, R.string.ft_s)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = m_s, engineering = m_s, si = m_s, imperial = ft_s)

private val _surfaceTensionUnits = listOf(
        UnitConverterWrapper(68, N_m, R.string.N_m),
        UnitConverterWrapper(69, kg_s2, R.string.kg_s2),
        UnitConverterWrapper(70, lbf_ft, R.string.lbf_ft)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = N_m, engineering = N_m, si = N_m, imperial = lbf_ft)

private val _thermalConductivityUnits = listOf(
        UnitConverterWrapper(71, W_mK, R.string.W_mK),
        UnitConverterWrapper(72, kW_mK, R.string.kW_mK),
        UnitConverterWrapper(73, BTU_hrftR, R.string.BTU_hrftR)
).also { allUnitMappings.addAll(it) } to
        DefaultUnits(default = W_mK, engineering = kW_mK, si = W_mK, imperial = BTU_hrftR)

private val _wavelengthUnits = listOf(
        UnitConverterWrapper(74, m, R.string.m),
        UnitConverterWrapper(75, cm, R.string.cm),
        UnitConverterWrapper(76, mm, R.string.mm),
        UnitConverterWrapper(77, mcm, R.string.mcm),
        UnitConverterWrapper(78, nm, R.string.nm),
        UnitConverterWrapper(79, inch, R.string.inch)
).also { allUnitMappings.addAll(it) }

private val unitUnitMapping = allUnitMappings.associate { it.unit to it }

private val computablePairs: List<Pair<QuantityWrapper, QuantityWrapper>> = listOf(
        Pressure to Temperature,
        Pressure to SpecificEnthalpy,
        Pressure to SpecificEntropy,
        SpecificEnthalpy to SpecificEntropy,
        Temperature to SpecificEntropy,
        Density to Temperature,
        SpecificVolume to Temperature,
        Pressure to VapourFraction,
        Temperature to VapourFraction
).map { (q1, q2) -> quantityQuantityMapping[q1]!! to quantityQuantityMapping[q2]!! }

val computablePropMap: Map<QuantityWrapper, List<QuantityWrapper>> = with(computablePairs.unzip()) {
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

class QuantityWrapper internal constructor(
        val id: Int,
        val quantity: Quantity,
        units: Pair<List<UnitConverterWrapper>, DefaultUnits>,
        val nameRes: Int,
        val symbolRes: Int
) {
    val units: List<UnitConverterWrapper> = units.first
    val defaultUnits: DefaultUnits = units.second

    override fun equals(other: Any?): Boolean {
        if (other !is QuantityWrapper) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}

class DefaultUnits internal constructor(
        default: UnitConverter,
        engineering: UnitConverter,
        si: UnitConverter,
        imperial: UnitConverter
) {
    val default: UnitConverterWrapper = unitUnitMapping[default]!!
    val engineering: UnitConverterWrapper = unitUnitMapping[engineering]!!
    val si: UnitConverterWrapper = unitUnitMapping[si]!!
    val imperial: UnitConverterWrapper = unitUnitMapping[imperial]!!
}

val allQuantityWrappers: List<QuantityWrapper> = listOf(
        QuantityWrapper(1, Pressure, _pressureUnits, R.string.Pressure, R.string.P),
        QuantityWrapper(2, Temperature, _temperatureUnits, R.string.Temperature, R.string.T),
        QuantityWrapper(3, SpecificEnthalpy, _specificEnergyUnits, R.string.SpecificEnthalpy, R.string.h),
        QuantityWrapper(4, SpecificEntropy, _specificEnergyUnits, R.string.SpecificEntropy, R.string.s),
        QuantityWrapper(5, VapourFraction, _ratioUnits, R.string.VapourFraction, R.string.x),
        QuantityWrapper(6, SpecificVolume, _specificVolumeUnits, R.string.SpecificVolume, R.string.v),
        QuantityWrapper(7, Density, _densityUnits, R.string.Density, R.string.rho),
        QuantityWrapper(8, SpeedOfSound, _speedUnits, R.string.SpeedOfSound, R.string.w),
        QuantityWrapper(9, SpecificIsobaricHeatCapacity, _specificHeatCapacityUnits, R.string.SpecificIsobaricHeatCapacity, R.string.cp),
        QuantityWrapper(10, SpecificIsochoricHeatCapacity, _specificHeatCapacityUnits, R.string.SpecificIsochoricHeatCapacity, R.string.cv),
        QuantityWrapper(11, SpecificEnthalpyOfVaporization, _specificEnergyUnits, R.string.SpecificEnthalpyOfVaporization, R.string.hvap),
        QuantityWrapper(12, ThermalConductivity, _thermalConductivityUnits, R.string.ThermalConductivity, R.string.lambda),
        QuantityWrapper(13, ThermalDiffusivity, _kinematicViscosityUnits, R.string.ThermalDiffusivity, R.string.k),
        QuantityWrapper(14, PrandtlNumber, _ratioUnits, R.string.PrandtlNumber, R.string.Pr),
        QuantityWrapper(15, DynamicViscosity, _dynamicViscosityUnits, R.string.DynamicViscosity, R.string.eta),
        QuantityWrapper(16, KinematicViscosity, _kinematicViscosityUnits, R.string.KinematicViscosity, R.string.nu),
        QuantityWrapper(17, SurfaceTension, _surfaceTensionUnits, R.string.SurfaceTension, R.string.sigma),
        QuantityWrapper(18, IsobaricCubicExpansionCoefficient, _temperature_1Units, R.string.IsobaricCubicExpansionCoefficient, R.string.av),
        QuantityWrapper(19, IsothermalCompressibility, _compressibilityUnits, R.string.IsothermalCompressibility, R.string.kT),
        QuantityWrapper(20, RelativePermittivity, _ratioUnits, R.string.RelativePermittivity, R.string.epsilon),
        QuantityWrapper(21, SpecificInternalEnergy, _specificEnergyUnits, R.string.SpecificInternalEnergy, R.string.u),
        QuantityWrapper(22, SpecificGibbsFreeEnergy, _specificEnergyUnits, R.string.SpecificGibbsFreeEnergy, R.string.g)
)

val quantityQuantityMapping = allQuantityWrappers.associate { it.quantity to it }

val quantityIdMap = allQuantityWrappers.associate { it.id to it }

private val quantityQuantityMap = allQuantityWrappers.associate { it.quantity to it }

class QuantityValueWrapper(internal val quantityValue: QuantityValue) {
    constructor(quantity: QuantityWrapper, value: Double, unit: UnitConverterWrapper) :
            this(QuantityValue(quantity.quantity, value, unit.unit.unit))

    operator fun get(unit: UnitConverterWrapper) = quantityValue[unit.unit]

    val value get() = quantityValue.value
    val quantity get() = quantityQuantityMapping[quantityValue.quantity]!!
    val unit get() = unitUnitMapping[quantityValue.unit]!!
}

class SteamWrapper(first: QuantityValueWrapper, second: QuantityValueWrapper) : Iterable<QuantityValueWrapper> {
    private val steam = Steam(
            first.quantityValue,
            second.quantityValue)

    constructor(): this(QuantityValueWrapper(Pressure(Double.NaN, Pa)), QuantityValueWrapper(Temperature(Double.NaN, K)))

    operator fun get(quantity: QuantityWrapper) = steam.get(quantity.quantity)

    override fun iterator(): Iterator<QuantityValueWrapper> {
        val original = steam.iterator()
        return object : Iterator<QuantityValueWrapper> {
            override fun hasNext(): Boolean = original.hasNext()
            override fun next(): QuantityValueWrapper = QuantityValueWrapper(original.next())
        }
    }
}
