package ru.lextop.steamcalculator.steam

import com.hummeling.if97.IF97
import ru.lextop.steamcalculator.steam.quantity.*
import ru.lextop.steamcalculator.steam.quantity.Units.Compressibility.Pa_1
import ru.lextop.steamcalculator.steam.quantity.Units.Density.kg_m3
import ru.lextop.steamcalculator.steam.quantity.Units.Pressure.Pa
import ru.lextop.steamcalculator.steam.quantity.Units.Ratio.ratio
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.J_kg
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature.K
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature_1.K_1
import ru.lextop.steamcalculator.steam.quantity.Units.DynamicViscosity.Pas
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificHeatCapacity.J_kgK
import ru.lextop.steamcalculator.steam.quantity.Units.KinematicViscosity.m2_s
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificVolume.m3_kg
import ru.lextop.steamcalculator.steam.quantity.Units.Speed.m_s
import ru.lextop.steamcalculator.steam.quantity.Units.SurfaceTension.N_m
import ru.lextop.steamcalculator.steam.quantity.Units.ThermalConductivity.W_mK
import java.lang.Double.NaN

open class Steam private constructor(protected val initQs: Triple<Quantity, Quantity, Double?>)
    : Iterable<Quantity> {
    constructor(arg1: Quantity, arg2: Quantity) : this(getTripleFromPropertyPair(arg1, arg2))

    constructor(rho: Quantity?,
                v: Quantity?,
                P: Quantity?,
                T: Quantity?,
                h: Quantity?,
                s: Quantity?,
                x: Quantity?) : this(getTripleOrThrowException(rho, v, P, T, h, s, x))

    val rho: Quantity by if97(Density, kg_m3) {
        initQs.tryEval(Density) { rho ->
            rho
        }.tryEval(SpecificVolume) { v ->
            1 / v
        }.tryEval(Pressure, Temperature) { p, t ->
            densityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            densityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            densityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            densityHS(h, s)
        }.tryEval(Pressure, VapourFraction) { p, x ->
            densityPX(p, x)
        }.tryEval(Temperature, VapourFraction) { t, x ->
            densityTX(t, x)
        }
    }

    val epsilon: Quantity by if97(RelativePermittivity, ratio) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            dielectricConstantPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            dielectricConstantPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            dielectricConstantPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            dielectricConstantHS(h, s)
        }.tryEval(Density, Temperature) { rho, t ->
            dielectricConstantRhoT(rho, t)
        }.tryEval(SpecificVolume, Temperature) { v, t ->
            dielectricConstantRhoT(1 / v, t)
        }
    }

    val eta: Quantity by if97(DynamicViscosity, Pas) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            dynamicViscosityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            dynamicViscosityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            dynamicViscosityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            dynamicViscosityHS(h, s)
        }.tryEval(Density, Temperature) { rho, t ->
            dynamicViscosityRhoT(rho, t)
        }.tryEval(SpecificVolume, Temperature) { v, t ->
            dynamicViscosityRhoT(1 / v, t)
        }
    }

    val av: Quantity by if97(IsobaricCubicExpansionCoefficient, K_1) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            isobaricCubicExpansionCoefficientPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            isobaricCubicExpansionCoefficientPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            isobaricCubicExpansionCoefficientPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            isobaricCubicExpansionCoefficientHS(h, s)
        }
    }

    val kT: Quantity by if97(IsothermalCompressibility, Pa_1) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            compressibilityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            compressibilityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            compressibilityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            compressibilityHS(h, s)
        }
    }

    val nu: Quantity by if97(KinematicViscosity, m2_s) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            kinematicViscosityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            kinematicViscosityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            kinematicViscosityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            kinematicViscosityHS(h, s)
        }.tryEval(Density, Temperature) { rho, t ->
            kinematicViscosityRhoT(rho, t)
        }.tryEval(SpecificVolume, Temperature) { v, t ->
            kinematicViscosityRhoT(1 / v, t)
        }
    }

    val Pr: Quantity by if97(IsothermalCompressibility, ratio) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            PrandtlPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            PrandtlPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            PrandtlPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            PrandtlHS(h, s)
        }
    }

    val P: Quantity by if97(Pressure, Pa) {
        initQs.tryEval(Pressure) { p ->
            p
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            pressureHS(h, s)
        }.tryEval(Temperature) { t ->
            saturationPressureT(t)
        }
    }

    /*
        val n: Quantity by if97(KinematicViscosity, m2_s) {
            initQs.tryEval(Pressure, Temperature) { p, t ->
                refractiveIndexPTLambda(p, t)
            }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
                kinematicViscosityPH(p, h)
            }.tryEval(Pressure, SpecificEntropy) { p, s ->
                kinematicViscosityPS(p, s)
            }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
                kinematicViscosityHS(h, s)
            }.tryEval(Density, Temperature) { rho, t ->
                kinematicViscosityRhoT(rho, t)
            }
        }
    */
    val h: Quantity by if97(SpecificEnthalpy, J_kg) {
        initQs.tryEval(SpecificEnthalpy) { h ->
            h
        }.tryEval(Pressure, Temperature) { p, t ->
            specificEnthalpyPT(p, t)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            specificEnthalpyPS(p, s)
        }.tryEval(Pressure, VapourFraction) { p, x ->
            specificEnthalpyPX(p, x)
        }.tryEval(Temperature, VapourFraction) { t, x ->
            specificEnthalpyTX(t, x)
        }.
                tryEval(Temperature, SpecificEntropy) { t, s ->
                    specificEnthalpyTX(t, vapourFractionTS(t, s))
                }
    }

    val u: Quantity by if97(SpecificInternalEnergy, J_kg) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            specificInternalEnergyPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            specificInternalEnergyPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            specificInternalEnergyPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            specificInternalEnergyHS(h, s)
        }
    }

    val s: Quantity by if97(SpecificEntropy, J_kgK) {
        initQs.tryEval(SpecificEntropy) { s ->
            s
        }.tryEval(Pressure, Temperature) { p, t ->
            specificEntropyPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            specificEntropyPH(p, h)
        }.tryEval(Pressure, VapourFraction) { p, x ->
            specificEntropyPX(p, x)
        }.tryEval(Temperature, VapourFraction) { t, x ->
            specificEntropyTX(t, x)
        }
    }

    val cp: Quantity by if97(SpecificIsobaricHeatCapacity, J_kgK) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            isobaricHeatCapacityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            isobaricHeatCapacityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            isobaricHeatCapacityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            isobaricHeatCapacityHS(h, s)
        }
    }

    val cv: Quantity by if97(SpecificIsochoricHeatCapacity, J_kgK) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            isochoricHeatCapacityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            isochoricHeatCapacityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            isobaricHeatCapacityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            isochoricHeatCapacityHS(h, s)
        }
    }

    val v: Quantity by if97(SpecificVolume, m3_kg) {
        initQs.tryEval(SpecificVolume) { v ->
            v
        }.tryEval(Density) { rho ->
            1 / rho
        }.tryEval(Pressure, Temperature) { p, t ->
            specificVolumePT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            specificVolumePH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            specificVolumePS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            specificVolumeHS(h, s)
        }.tryEval(Pressure, VapourFraction) { p, x ->
            specificVolumePX(p, x)
        }.tryEval(Temperature, VapourFraction) { t, x ->
            specificVolumeTX(t, x)
        }
    }

    val w: Quantity by if97(SpeedOfSound, m_s) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            speedOfSoundPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            speedOfSoundPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            speedOfSoundPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            speedOfSoundHS(h, s)
        }
    }

    val sigma: Quantity by if97(SurfaceTension, N_m) {
        initQs.tryEval(Pressure) { p ->
            surfaceTensionP(p)
        }.tryEval(Temperature) { t ->
            surfaceTensionT(t)
        }
    }

    val T: Quantity by if97(Temperature, K) {
        initQs.tryEval(Temperature) { t ->
            t
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            temperaturePH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            temperaturePS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            temperatureHS(h, s)
        }.tryEval(Pressure) { p ->
            saturationTemperatureP(p)
        }
    }

    val lambda: Quantity by if97(ThermalConductivity, W_mK) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            thermalConductivityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            thermalConductivityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            thermalConductivityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            thermalConductivityHS(h, s)
        }.tryEval(Density, Temperature) { rho, t ->
            thermalConductivityRhoT(rho, t)
        }.tryEval(SpecificVolume, Temperature) { v, t ->
            thermalConductivityRhoT(1 / v, t)
        }
    }

    val k: Quantity by if97(ThermalDiffusivity, m2_s) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            thermalDiffusivityPT(p, t)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            thermalDiffusivityPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            thermalDiffusivityPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            thermalDiffusivityHS(h, s)
        }
    }

    val x: Quantity by if97(VapourFraction, ratio) {
        initQs.tryEval(VapourFraction) { x ->
            x
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            vapourFractionPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            vapourFractionPS(p, s)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            vapourFractionHS(h, s)
        }.tryEval(Temperature, SpecificEntropy) { t, s ->
            vapourFractionTS(t, s)
        }
    }

    val g: Quantity by if97(SpecificGibbsFreeEnergy, J_kg) {
        initQs.tryEval(Pressure, Temperature) { p, t ->
            specificGibbsFreeEnergyPT(p, t)
        }
    }

    val hvap: Quantity by lazy {
        if (x.value.isNaN() || x.value < 0 || x.value > 1) {
            SpecificEnthalpyOfVaporization(Double.NaN, J_kg)
        } else {
            val liq = if97Instance.specificEnthalpySaturatedLiquidP(P.value)
            val vap = if97Instance.specificEnthalpySaturatedVapourP(P.value)
            SpecificEnthalpyOfVaporization(vap - liq, J_kg)
        }
    }

    open protected val quantities: List<Quantity> by lazy {
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

    protected inline fun Triple<Quantity, Quantity, Double?>.tryEval(
            prop1: Property,
            calculate: (q1: Double) -> Double)
            : Triple<Quantity, Quantity, Double?> {
        if (third != null) return this
        val (arg1, arg2) = this
        val q1: Quantity
        q1 = when (prop1) {
            arg1.property -> arg1
            arg2.property -> arg2
            else -> return this
        }
        return copy(third = try {
            calculate(q1.value)
        } catch (e: Exception) {  // OutOfRangeException
            return this
        })
    }

    protected inline fun Triple<Quantity, Quantity, Double?>.tryEval(
            prop1: Property,
            prop2: Property,
            calculate: (q1: Double, q2: Double) -> Double)
            : Triple<Quantity, Quantity, Double?> {
        if (third != null) return this
        val (arg1, arg2) = this
        val q1: Quantity
        val q2: Quantity
        if (arg1.property == prop1 && arg2.property == prop2) {
            q1 = arg1
            q2 = arg2
        } else if (arg2.property == prop1 && arg1.property == prop2) {
            q1 = arg2
            q2 = arg1
        } else {
            return this
        }
        return copy(third = try {
            calculate(q1.value, q2.value)
        } catch (e: Exception) {  // OutOfRangeException
            return this
        })
    }

    private companion object {
        private val if97Instance = IF97(IF97.UnitSystem.SI)
        private fun if97(
                type: Property,
                unit: UnitPh,
                eval: IF97.() -> Triple<Quantity, Quantity, Double?>): Lazy<Quantity> =
                lazy { type(if97Instance.eval().third ?: NaN, unit) }

        @Suppress("UNCHECKED_CAST")
        private inline fun getTripleFromPropertyPair(arg1: Quantity, arg2: Quantity) = Triple<Quantity, Quantity, Double?>(
                arg1[arg1.property.baseUnit.alias],
                arg2[arg2.property.baseUnit.alias],
                null)

        private fun getTripleOrThrowException(
                rho: Quantity?,
                v: Quantity?,
                P: Quantity?,
                T: Quantity?,
                h: Quantity?,
                s: Quantity?,
                x: Quantity?
        ): Triple<Quantity, Quantity, Double?> {
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
                    else -> throw RuntimeException("There must be exactly 2 parameters")
                }
            }
            if (T != null) {
                quantity = Temperature(T.value, T.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw RuntimeException("There must be exactly 2 parameters")
                }
            }
            if (h != null) {
                quantity = SpecificEnthalpy(h.value, h.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw RuntimeException("There must be exactly 2 parameters")
                }
            }
            if (s != null) {
                quantity = SpecificEntropy(s.value, s.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw RuntimeException("There must be exactly 2 parameters")
                }
            }
            if (x != null) {
                quantity = VapourFraction(x.value, x.unit)
                when {
                    q1 == null -> q1 = quantity
                    q2 == null -> q2 = quantity
                    else -> throw RuntimeException("There must be exactly 2 parameters")
                }
            }
            if (q1 == null || q2 == null) throw RuntimeException("There must be exactly 2 parameters")
            return getTripleFromPropertyPair(q1, q2)
        }
    }
}