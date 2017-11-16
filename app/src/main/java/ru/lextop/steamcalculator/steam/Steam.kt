package ru.lextop.steamcalculator.steam

import com.hummeling.if97.IF97
import ru.lextop.steamcalculator.steam.quantity.*
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.J_kg

class Steam private constructor(pair: Pair<Quantity, Quantity>)
    : Iterable<Quantity> {
    private val value1: Double
    private val value2: Double
    private val computablePairProps: Pair<Property, Property>

    init {
        val computablePair = getComputablePair(pair)
        value1 = computablePair.first.value
        value2 = computablePair.second.value
        computablePairProps = computablePair.first.property to computablePair.second.property
    }

    private fun if97(p: Property): Lazy<Quantity> = lazy {
        p(try {
            calcMap[computablePairProps]!![p]!!(value1, value2)
        } catch (e: Exception) {
            if (computablePairProps.second == VapourFraction) {
                try {
                    calcMap[SpecificEnthalpy to SpecificEntropy]!![p]!!(h.value, s.value)
                } catch (e: Exception) {
                    Double.NaN
                }
            } else {
                Double.NaN
            }
        }, p.baseUnit.alias)
    }

    constructor(arg1: Quantity, arg2: Quantity) : this(arg1 to arg2)

    constructor(rho: Quantity?,
                v: Quantity?,
                P: Quantity?,
                T: Quantity?,
                h: Quantity?,
                s: Quantity?,
                x: Quantity?) : this(getPairOrThrowException(rho, v, P, T, h, s, x))

    operator fun get(p: Property): Quantity = if97(p).value

    val rho: Quantity by if97(Density)
    val epsilon: Quantity by if97(RelativePermittivity)
    val eta: Quantity by if97(DynamicViscosity)
    val av: Quantity by if97(IsobaricCubicExpansionCoefficient)
    val kT: Quantity by if97(IsothermalCompressibility)
    val nu: Quantity by if97(KinematicViscosity)
    val Pr: Quantity by if97(IsothermalCompressibility)
    val P: Quantity by if97(Pressure)
    /*
        val n: Quantity by if97(KinematicViscosity)
    */
    val h: Quantity by if97(SpecificEnthalpy)
    val u: Quantity by if97(SpecificInternalEnergy)
    val s: Quantity by if97(SpecificEntropy)
    val cp: Quantity by if97(SpecificIsobaricHeatCapacity)
    val cv: Quantity by if97(SpecificIsochoricHeatCapacity)
    val v: Quantity by if97(SpecificVolume)
    val w: Quantity by if97(SpeedOfSound)
    val sigma: Quantity by if97(SurfaceTension)
    val T: Quantity by if97(Temperature)
    val lambda: Quantity by if97(ThermalConductivity)
    val k: Quantity by if97(ThermalDiffusivity)
    val x: Quantity by if97(VapourFraction)
    val g: Quantity by if97(SpecificGibbsFreeEnergy)
    val hvap: Quantity by lazy {
        if (x.value.isNaN() || x.value < 0 || x.value > 1) {
            SpecificEnthalpyOfVaporization(Double.NaN, J_kg)
        } else {
            val liq = if97Instance.specificEnthalpySaturatedLiquidP(P.value)
            val vap = if97Instance.specificEnthalpySaturatedVapourP(P.value)
            SpecificEnthalpyOfVaporization(vap - liq, J_kg)
        }
    }

    private val quantities: List<Quantity> by lazy {
        listOf(
                rho, epsilon, eta, av, kT, nu, Pr, P, /*n,*/ h, u, s, cp, cv, v, w, sigma, T, lambda, k, x, g, hvap
        )
    }

    override fun toString(): String = quantities.toString()
    override fun iterator(): Iterator<Quantity> = object : Iterator<Quantity> {
        val iterator = quantities.iterator()
        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): Quantity = iterator.next()
    }

    private companion object {
        private val if97Instance = IF97(IF97.UnitSystem.SI)

        private val calcMap: Map<Pair<Property, Property>, Map<Property, (Double, Double) -> Double>> = with(if97Instance) {
            mapOf(
                    Pressure to Temperature to mapOf(
                            Density to this::densityPT,
                            RelativePermittivity to this::dielectricConstantPT,
                            DynamicViscosity to this::dynamicViscosityPT,
                            IsobaricCubicExpansionCoefficient to this::isobaricCubicExpansionCoefficientPT,
                            IsothermalCompressibility to this::compressibilityPT,
                            KinematicViscosity to this::kinematicViscosityPT,
                            PrandtlNumber to this::PrandtlPT,
                            Pressure to { p: Double, _: Double -> p },
                            SpecificEnthalpy to this::specificEnthalpyPT,
                            SpecificInternalEnergy to this::specificInternalEnergyPT,
                            SpecificEntropy to this::specificEntropyPT,
                            SpecificIsobaricHeatCapacity to this::isobaricHeatCapacityPT,
                            SpecificIsochoricHeatCapacity to this::isochoricHeatCapacityPT,
                            SpecificVolume to this::specificVolumePT,
                            SpeedOfSound to this::speedOfSoundPT,
                            //SurfaceTension to ,
                            Temperature to { _: Double, t: Double -> t },
                            ThermalConductivity to this::thermalConductivityPT,
                            ThermalDiffusivity to this::thermalDiffusivityPT,
                            VapourFraction to { _, _ -> Double.NaN },
                            SpecificGibbsFreeEnergy to this::specificGibbsFreeEnergyPT
                    ),
                    Pressure to SpecificEnthalpy to mapOf(
                            Density to this::densityPH,
                            RelativePermittivity to this::dielectricConstantPH,
                            DynamicViscosity to this::dynamicViscosityPH,
                            IsobaricCubicExpansionCoefficient to this::isobaricCubicExpansionCoefficientPH,
                            IsothermalCompressibility to this::compressibilityPH,
                            KinematicViscosity to this::kinematicViscosityPH,
                            PrandtlNumber to this::PrandtlPH,
                            Pressure to { p: Double, _: Double -> p },
                            SpecificEnthalpy to { _: Double, h: Double -> h },
                            SpecificInternalEnergy to this::specificInternalEnergyPH,
                            SpecificEntropy to this::specificEntropyPH,
                            SpecificIsobaricHeatCapacity to this::isobaricHeatCapacityPH,
                            SpecificIsochoricHeatCapacity to this::isochoricHeatCapacityPH,
                            SpecificVolume to this::specificVolumePH,
                            SpeedOfSound to this::speedOfSoundPH,
                            //SurfaceTension to ,
                            Temperature to this::temperaturePH,
                            ThermalConductivity to this::thermalConductivityPH,
                            ThermalDiffusivity to this::thermalDiffusivityPH,
                            VapourFraction to this::vapourFractionPH
                            //SpecificGibbsFreeEnergy to
                    ),
                    Pressure to SpecificEntropy to mapOf(
                            Density to this::densityPS,
                            RelativePermittivity to this::dielectricConstantPS,
                            DynamicViscosity to this::dynamicViscosityPS,
                            IsobaricCubicExpansionCoefficient to this::isobaricCubicExpansionCoefficientPS,
                            IsothermalCompressibility to this::compressibilityPS,
                            KinematicViscosity to this::kinematicViscosityPS,
                            PrandtlNumber to this::PrandtlPS,
                            Pressure to { p: Double, _: Double -> p },
                            SpecificEnthalpy to this::specificEnthalpyPS,
                            SpecificInternalEnergy to this::specificInternalEnergyPS,
                            SpecificEntropy to { _: Double, s: Double -> s },
                            SpecificIsobaricHeatCapacity to this::isobaricHeatCapacityPS,
                            SpecificIsochoricHeatCapacity to this::isochoricHeatCapacityPS,
                            SpecificVolume to this::specificVolumePS,
                            SpeedOfSound to this::speedOfSoundPS,
                            //SurfaceTension to ,
                            Temperature to this::temperaturePS,
                            ThermalConductivity to this::thermalConductivityPS,
                            ThermalDiffusivity to this::thermalDiffusivityPS,
                            VapourFraction to this::vapourFractionPS
                            //SpecificGibbsFreeEnergy to
                    ),
                    SpecificEnthalpy to SpecificEntropy to mapOf(
                            Density to this::densityHS,
                            RelativePermittivity to this::dielectricConstantHS,
                            DynamicViscosity to this::dynamicViscosityHS,
                            IsobaricCubicExpansionCoefficient to this::isobaricCubicExpansionCoefficientHS,
                            IsothermalCompressibility to this::compressibilityHS,
                            KinematicViscosity to this::kinematicViscosityHS,
                            PrandtlNumber to this::PrandtlHS,
                            Pressure to this::pressureHS,
                            SpecificEnthalpy to { h: Double, _: Double -> h },
                            SpecificInternalEnergy to this::specificInternalEnergyHS,
                            SpecificEntropy to { _: Double, s: Double -> s },
                            SpecificIsobaricHeatCapacity to this::isobaricHeatCapacityHS,
                            SpecificIsochoricHeatCapacity to this::isochoricHeatCapacityHS,
                            SpecificVolume to this::specificVolumeHS,
                            SpeedOfSound to this::speedOfSoundHS,
                            //SurfaceTension to ,
                            Temperature to this::temperatureHS,
                            ThermalConductivity to this::thermalConductivityHS,
                            ThermalDiffusivity to this::thermalDiffusivityHS,
                            VapourFraction to this::vapourFractionHS
                            //SpecificGibbsFreeEnergy to
                    ),
                    Temperature to SpecificEntropy to mapOf(
                            VapourFraction to this::vapourFractionTS
                    ),
                    Density to Temperature to mapOf(
                            Density to { rho: Double, _: Double -> rho },
                            RelativePermittivity to this::dielectricConstantRhoT,
                            DynamicViscosity to this::dynamicViscosityRhoT,
                            //IsobaricCubicExpansionCoefficient to this::isobaricCubicExpansionCoefficientRhoT,
                            //IsothermalCompressibility to this::compressibilityRhoT,
                            KinematicViscosity to this::kinematicViscosityRhoT,
                            //PrandtlNumber to this::PrandtlRhoT,
                            //Pressure to this::pressureRhoT,
                            //SpecificEnthalpy to ,
                            //SpecificInternalEnergy to this::specificInternalEnergyRhoT,
                            //SpecificEntropy to ,
                            //SpecificIsobaricHeatCapacity to this::isobaricHeatCapacityRhoT,
                            //SpecificIsochoricHeatCapacity to this::isochoricHeatCapacityRhoT,
                            //SpecificVolume to this::specificVolumeRhoT,
                            //SpeedOfSound to this::speedOfSoundRhoT,
                            //SurfaceTension to ,
                            Temperature to { _: Double, t: Double -> t },
                            ThermalConductivity to this::thermalConductivityRhoT
                            //ThermalDiffusivity to this::thermalDiffusivityRhoT,
                            //VapourFraction to this::vapourFractionRhoT
                            //SpecificGibbsFreeEnergy to
                    ),
                    Pressure to VapourFraction to mapOf(
                            Density to this::densityPX,
                            //RelativePermittivity to this::dielectricConstantPX,
                            //DynamicViscosity to this::dynamicViscosityPX,
                            //IsobaricCubicExpansionCoefficient to this::isobaricCubicExpansionCoefficientPX,
                            //IsothermalCompressibility to this::compressibilityPX,
                            //KinematicViscosity to this::kinematicViscosityPX,
                            //PrandtlNumber to this::PrandtlPX,
                            Pressure to { p: Double, _: Double -> p },
                            SpecificEnthalpy to this::specificEnthalpyPX,
                            //SpecificInternalEnergy to this::specificInternalEnergyPX,
                            SpecificEntropy to this::specificEntropyPX,
                            //SpecificIsobaricHeatCapacity to this::isobaricHeatCapacityPX,
                            //SpecificIsochoricHeatCapacity to this::isochoricHeatCapacityPX,
                            SpecificVolume to this::specificVolumePX,
                            //SpeedOfSound to this::speedOfSoundPX,
                            SurfaceTension to { p: Double, _: Double -> surfaceTensionP(p) },
                            Temperature to { p: Double, _: Double -> saturationTemperatureP(p) },
                            //ThermalConductivity to this::thermalConductivityPX,
                            //ThermalDiffusivity to this::thermalDiffusivityPX,
                            VapourFraction to { _: Double, x: Double -> x }
                            //SpecificGibbsFreeEnergy to this::specificGibbsFreeEnergyPX
                    ),
                    Temperature to VapourFraction to mapOf(
                            Density to this::densityTX,
                            //RelativePermittivity to this::dielectricConstantTX,
                            //DynamicViscosity to this::dynamicViscosityTX,
                            //IsobaricCubicExpansionCoefficient to this::isobaricCubicExpansionCoefficientTX,
                            //IsothermalCompressibility to this::compressibilityTX,
                            //KinematicViscosity to this::kinematicViscosityTX,
                            //PrandtlNumber to this::PrandtlTX,
                            Pressure to { t: Double, _: Double -> saturationPressureT(t) },
                            SpecificEnthalpy to this::specificEnthalpyTX,
                            //SpecificInternalEnergy to this::specificInternalEnergyTX,
                            SpecificEntropy to this::specificEntropyTX,
                            //SpecificIsobaricHeatCapacity to this::isobaricHeatCapacityTX,
                            //SpecificIsochoricHeatCapacity to this::isochoricHeatCapacityTX,
                            SpecificVolume to this::specificVolumeTX,
                            //SpeedOfSound to this::speedOfSoundTX,
                            SurfaceTension to { t: Double, _: Double -> surfaceTensionT(t) },
                            Temperature to { t: Double, _: Double -> t },
                            //ThermalConductivity to this::thermalConductivityTX,
                            //ThermalDiffusivity to this::thermalDiffusivityTX,
                            VapourFraction to { _: Double, x: Double -> x }
                            //SpecificGibbsFreeEnergy to this::specificGibbsFreeEnergyTX
                    )
            )
        }

        private val computablePairs: Set<Pair<Property, Property>> = setOf(
                Pressure to Temperature,
                Pressure to SpecificEnthalpy,
                Pressure to SpecificEntropy,
                SpecificEnthalpy to SpecificEntropy,
                Temperature to SpecificEntropy,
                Density to Temperature,
                Pressure to VapourFraction,
                Temperature to VapourFraction
        )

        private fun getComputablePair(pair: Pair<Quantity, Quantity>): Pair<Quantity, Quantity> {
            val (arg1, arg2) = pair
            val baseArg1 = arg1[arg1.property.baseUnit.alias]
            val baseArg2 = arg2[arg2.property.baseUnit.alias]
            val q1: Quantity
            val q2: Quantity
            when (SpecificVolume) {
                baseArg1.property -> {
                    q1 = Density(baseArg1.value, Density.baseUnit.alias)
                    q2 = baseArg2
                }
                baseArg2.property -> {
                    q1 = baseArg1
                    q2 = Density(baseArg2.value, Density.baseUnit.alias)
                }
                else -> {
                    q1 = baseArg1
                    q2 = baseArg2
                }
            }
            return when {
                q1.property to q2.property in computablePairs -> q1 to q2
                q2.property to q1.property in computablePairs -> q2 to q1
                else -> throw IllegalArgumentException()
            }
        }

        private fun getPairOrThrowException(
                rho: Quantity?,
                v: Quantity?,
                P: Quantity?,
                T: Quantity?,
                h: Quantity?,
                s: Quantity?,
                x: Quantity?
        ): Pair<Quantity, Quantity> {
            var q1: Quantity? = null
            var q2: Quantity? = null
            var quantity: Quantity
            if (rho != null) q1 = Density(rho.value, rho.unit)
            if (v != null) {
                quantity = SpecificVolume(v.value, v.unit)
                when {
                    q1 == null -> q1 = quantity
                    else -> q2 = quantity
                }
            }
            if (P != null) {
                quantity = Pressure(P.value, P.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw IllegalArgumentException()
                }
            }
            if (T != null) {
                quantity = Temperature(T.value, T.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw IllegalArgumentException()
                }
            }
            if (h != null) {
                quantity = SpecificEnthalpy(h.value, h.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw IllegalArgumentException()
                }
            }
            if (s != null) {
                quantity = SpecificEntropy(s.value, s.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw IllegalArgumentException()
                }
            }
            if (x != null) {
                quantity = VapourFraction(x.value, x.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw IllegalArgumentException()
                }
            }
            if (q1 == null || q2 == null) throw IllegalArgumentException()
            return q1 to q2
        }
    }
}