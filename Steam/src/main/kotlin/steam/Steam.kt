package steam

import com.hummeling.if97.IF97
import com.hummeling.if97.OutOfRangeException
import quantityvalue.*
import steam.quantities.*
import steam.units.*
import java.util.*

class Steam private constructor(pair: Pair<QuantityValue, QuantityValue>)
    : Iterable<QuantityValue> {
    private val value1: Double
    private val value2: Double
    private val computablePairProps: Pair<Quantity, Quantity>

    init {
        val computablePair = getComputablePair(pair)
        value1 = computablePair.first.value
        value2 = computablePair.second.value
        computablePairProps = computablePair.first.quantity to computablePair.second.quantity
    }

    private fun if97(p: Quantity): Lazy<QuantityValue> = lazy {
        p(try {
            CALC_MAP[computablePairProps]!![p]!!(value1, value2)
        } catch (e: Exception) {
            if (computablePairProps.second == VapourFraction) {
                try {
                    CALC_MAP[SpecificEnthalpy to SpecificEntropy]!![p]!!(h.value, s.value)
                } catch (e: Exception) {
                    Double.NaN
                }
            } else {
                Double.NaN
            }
        }, DEFAULT_UNITS[p]!!)
    }

    constructor(arg1: QuantityValue, arg2: QuantityValue) : this(arg1 to arg2)

    constructor(rho: QuantityValue?,
                v: QuantityValue?,
                P: QuantityValue?,
                T: QuantityValue?,
                h: QuantityValue?,
                s: QuantityValue?,
                x: QuantityValue?) : this(getPairOrThrowException(rho, v, P, T, h, s, x))

    operator fun get(p: Quantity): QuantityValue = if97(p).value

    val rho: QuantityValue by if97(Density)
    val epsilon: QuantityValue by if97(RelativePermittivity)
    val eta: QuantityValue by if97(DynamicViscosity)
    val av: QuantityValue by if97(IsobaricCubicExpansionCoefficient)
    val kT: QuantityValue by if97(IsothermalCompressibility)
    val nu: QuantityValue by if97(KinematicViscosity)
    val Pr: QuantityValue by if97(IsothermalCompressibility)
    val P: QuantityValue by if97(Pressure)
    /*
        val n: QuantityValue by if97(KinematicViscosity)
    */
    val h: QuantityValue by if97(SpecificEnthalpy)
    val u: QuantityValue by if97(SpecificInternalEnergy)
    val s: QuantityValue by if97(SpecificEntropy)
    val cp: QuantityValue by if97(SpecificIsobaricHeatCapacity)
    val cv: QuantityValue by if97(SpecificIsochoricHeatCapacity)
    val v: QuantityValue by if97(SpecificVolume)
    val w: QuantityValue by if97(SpeedOfSound)
    val sigma: QuantityValue by if97(SurfaceTension)
    val T: QuantityValue by if97(Temperature)
    val lambda: QuantityValue by if97(ThermalConductivity)
    val k: QuantityValue by if97(ThermalDiffusivity)
    val x: QuantityValue by if97(VapourFraction)
    val g: QuantityValue by if97(SpecificGibbsFreeEnergy)
    val hvap: QuantityValue by lazy {
        if (x.value.isNaN() || x.value < 0 || x.value > 1) {
            SpecificEnthalpyOfVaporization(Double.NaN, J_kg)
        } else {
            try {
                val liq = IF97_INSTANCE.specificEnthalpySaturatedLiquidP(P.value)
                val vap = IF97_INSTANCE.specificEnthalpySaturatedVapourP(P.value)
                SpecificEnthalpyOfVaporization(vap - liq, J_kg)
            } catch (e: OutOfRangeException) {
                SpecificEnthalpyOfVaporization(Double.NaN, J_kg)
            }
        }
    }

    fun refractiveIndex(wavelength: QuantityValue): QuantityValue {
        if (wavelength.quantity != Wavelength) throw IllegalArgumentException()
        val doubleResult = CALC_REFRACTIVE_INDEX_MAP[computablePairProps]!!.invoke(value1, value2, wavelength[m].value)
        return QuantityValue(Wavelength, doubleResult, m)
    }

    private val quantityValues: List<QuantityValue> by lazy {
        listOf(
                rho, epsilon, eta, av, kT, nu, Pr, P, /*n,*/ h, u, s, cp, cv, v, w, sigma, T, lambda, k, x, g, hvap
        )
    }

    override fun toString(): String = quantityValues.toString()
    override fun iterator(): Iterator<QuantityValue> = object : Iterator<QuantityValue> {
        val iterator = quantityValues.iterator()
        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): QuantityValue = iterator.next()
    }

    private companion object {
        private val IF97_INSTANCE = IF97(IF97.UnitSystem.SI)
        private val IF97_QUANTITY_MAP: Map<com.hummeling.if97.IF97.Quantity, Quantity> =
                EnumMap<com.hummeling.if97.IF97.Quantity, Quantity>(com.hummeling.if97.IF97.Quantity::class.java).apply {
                    put(com.hummeling.if97.IF97.Quantity.p, Pressure)
                    put(com.hummeling.if97.IF97.Quantity.T, Temperature)
                    put(com.hummeling.if97.IF97.Quantity.v, SpecificVolume)
                    put(com.hummeling.if97.IF97.Quantity.rho, Density)
                    put(com.hummeling.if97.IF97.Quantity.u, SpecificInternalEnergy)
                    put(com.hummeling.if97.IF97.Quantity.h, SpecificEnthalpy)
                    put(com.hummeling.if97.IF97.Quantity.s, SpecificEntropy)
                    //put(com.hummeling.if97.IF97.Quantity.lambda, Wavelength)
                    put(com.hummeling.if97.IF97.Quantity.g, SpecificGibbsFreeEnergy)
                    //put(com.hummeling.if97.IF97.Quantity.f, SpecificHelmholtzFreeEnergy)
                    put(com.hummeling.if97.IF97.Quantity.x, VapourFraction)
                }

        private val CALC_MAP: Map<Pair<Quantity, Quantity>, Map<Quantity, (Double, Double) -> Double>> = with(IF97_INSTANCE) {
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
                            SpecificVolume to { rho: Double, _: Double -> 1 / rho },
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

        private val CALC_REFRACTIVE_INDEX_MAP: Map<Pair<Quantity, Quantity>, (Double, Double, Double) -> Double> = with(IF97_INSTANCE){
            mapOf(
                    Pressure to Temperature to this::refractiveIndexPTLambda,
                    Pressure to SpecificEnthalpy to this::refractiveIndexPHLambda,
                    Pressure to SpecificEntropy to this::refractiveIndexPSLambda,
                    SpecificEnthalpy to SpecificEntropy to this::refractiveIndexHSLambda,
                    Density to Temperature to this::refractiveIndexRhoTLambda
            )
        }

        private val COMPUTABLE_PAIRS: Set<Pair<Quantity, Quantity>> = setOf(
                Pressure to Temperature,
                Pressure to SpecificEnthalpy,
                Pressure to SpecificEntropy,
                SpecificEnthalpy to SpecificEntropy,
                Temperature to SpecificEntropy,
                Density to Temperature,
                Pressure to VapourFraction,
                Temperature to VapourFraction
        )

        private val DEFAULT_UNITS: Map<Quantity, UnitPh> = mapOf(
                Pressure to Pa,
                Temperature to K,
                SpecificEnthalpy to J_kg,
                SpecificEntropy to J_kgK,
                VapourFraction to ratio,
                SpecificVolume to m3_kg,
                Density to kg_m3,
                SpeedOfSound to m_s,
                SpecificIsobaricHeatCapacity to J_kgK,
                SpecificIsochoricHeatCapacity to J_kgK,
                SpecificEnthalpyOfVaporization to J_kg,
                ThermalConductivity to W_mK,
                ThermalDiffusivity to m2_s,
                PrandtlNumber to ratio,
                DynamicViscosity to Pas,
                KinematicViscosity to m2_s,
                SurfaceTension to N_m,
                IsobaricCubicExpansionCoefficient to K_1,
                IsothermalCompressibility to Pa_1,
                RelativePermittivity to ratio,
                SpecificInternalEnergy to J_kg,
                SpecificGibbsFreeEnergy to J_kg
        )

        private fun getComputablePair(pair: Pair<QuantityValue, QuantityValue>): Pair<QuantityValue, QuantityValue> {
            val (arg1, arg2) = pair
            val baseArg1 = arg1[DEFAULT_UNITS[arg1.quantity]!!]
            val baseArg2 = arg2[DEFAULT_UNITS[arg2.quantity]!!]
            val q1: QuantityValue
            val q2: QuantityValue
            when (SpecificVolume) {
                baseArg1.quantity -> {
                    q1 = Density(1 / baseArg1.value, kg_m3)
                    q2 = baseArg2
                }
                baseArg2.quantity -> {
                    q1 = baseArg1
                    q2 = Density(1 / baseArg2.value, kg_m3)
                }
                else -> {
                    if (baseArg1.quantity == Temperature && baseArg2.quantity == SpecificEntropy) {
                        q1 = baseArg1
                        q2 = VapourFraction(try {
                            IF97_INSTANCE.vapourFractionTS(baseArg1.value, baseArg2.value)
                        } catch (e: OutOfRangeException) {
                            Double.NaN
                        }, ratio)
                    } else if (baseArg1.quantity == SpecificEntropy && baseArg2.quantity == Temperature) {
                        q1 = baseArg2
                        q2 = VapourFraction(try {
                            IF97_INSTANCE.vapourFractionTS(baseArg2.value, baseArg1.value)
                        } catch (e: OutOfRangeException) {
                            Double.NaN
                        }, ratio)
                    } else {
                        q1 = baseArg1
                        q2 = baseArg2
                    }
                }
            }
            return when {
                q1.quantity to q2.quantity in COMPUTABLE_PAIRS -> q1 to q2
                q2.quantity to q1.quantity in COMPUTABLE_PAIRS -> q2 to q1
                else -> throw IllegalArgumentException()
            }
        }

        private fun getPairOrThrowException(
                rho: QuantityValue?,
                v: QuantityValue?,
                P: QuantityValue?,
                T: QuantityValue?,
                h: QuantityValue?,
                s: QuantityValue?,
                x: QuantityValue?
        ): Pair<QuantityValue, QuantityValue> {
            var q1: QuantityValue? = null
            var q2: QuantityValue? = null
            var quantityValue: QuantityValue
            if (rho != null) q1 = Density(rho.value, rho.unit)
            if (v != null) {
                quantityValue = SpecificVolume(v.value, v.unit)
                when {
                    q1 == null -> q1 = quantityValue
                    else -> q2 = quantityValue
                }
            }
            if (P != null) {
                quantityValue = Pressure(P.value, P.unit)
                when {
                    q1 == null -> q1 = quantityValue
                    q2 == null -> q2 = quantityValue
                    else -> throw IllegalArgumentException()
                }
            }
            if (T != null) {
                quantityValue = Temperature(T.value, T.unit)
                when {
                    q1 == null -> q1 = quantityValue
                    q2 == null -> q2 = quantityValue
                    else -> throw IllegalArgumentException()
                }
            }
            if (h != null) {
                quantityValue = SpecificEnthalpy(h.value, h.unit)
                when {
                    q1 == null -> q1 = quantityValue
                    q2 == null -> q2 = quantityValue
                    else -> throw IllegalArgumentException()
                }
            }
            if (s != null) {
                quantityValue = SpecificEntropy(s.value, s.unit)
                when {
                    q1 == null -> q1 = quantityValue
                    q2 == null -> q2 = quantityValue
                    else -> throw IllegalArgumentException()
                }
            }
            if (x != null) {
                quantityValue = VapourFraction(x.value, x.unit)
                when {
                    q1 == null -> q1 = quantityValue
                    q2 == null -> q2 = quantityValue
                    else -> throw IllegalArgumentException()
                }
            }
            if (q1 == null || q2 == null) throw IllegalArgumentException()
            return q1 to q2
        }
    }
}