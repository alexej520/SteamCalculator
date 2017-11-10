package ru.lextop.steamcalculator.steam

import com.hummeling.if97.IF97
import ru.lextop.steamcalculator.steam.quantity.*
import ru.lextop.steamcalculator.steam.quantity.PressureUnit.Pa
import ru.lextop.steamcalculator.steam.quantity.RatioUnit.ratio
import ru.lextop.steamcalculator.steam.quantity.SpecificEnergyUnit.J_kg
import ru.lextop.steamcalculator.steam.quantity.TemperatureUnit.K
import java.lang.Double.NaN

open class Steam private constructor(protected val initQs: Triple<Quantity, Quantity, Double?>)
    : Iterable<Quantity> {
    constructor(arg1: Quantity, arg2: Quantity) : this(getTripleFromPropertyPair(arg1, arg2))

    constructor(s: Quantity? = null,
                x: Quantity? = null,
                P: Quantity? = null,
                T: Quantity? = null,
                h: Quantity? = null) : this(getTripleOrThrowException(s, x, P, T, h))

    val P: Quantity by if97(Pressure, Pa) {
        initQs.tryEval(Pressure) { p ->
            p
        }.tryEval(Temperature) { t ->
            saturationPressureT(t)
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            pressureHS(h, s)
        }
    }
    val T: Quantity by if97(Temperature, K) {
        initQs.tryEval(Temperature) { t ->
            t
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            temperatureHS(h, s)
        }.tryEval(Pressure) { p ->
            saturationTemperatureP(p)
        }
    }
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
        }.tryEval(Temperature, SpecificEntropy) { t, s ->
            specificEnthalpyTX(t, vapourFractionTS(t, s))
        }
    }
    val s: Quantity by if97(SpecificEntropy, J_kg) {
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
    val x: Quantity by if97(VapourFraction, ratio) {
        initQs.tryEval(VapourFraction) { x ->
            x
        }.tryEval(SpecificEnthalpy, SpecificEntropy) { h, s ->
            vapourFractionHS(h, s)
        }.tryEval(Pressure, SpecificEnthalpy) { p, h ->
            vapourFractionPH(p, h)
        }.tryEval(Pressure, SpecificEntropy) { p, s ->
            vapourFractionPS(p, s)
        }.tryEval(Temperature, SpecificEntropy) { t, s ->
            vapourFractionTS(t, s)
        }
    }
    open protected val quantities: List<Quantity> by lazy {
        listOf(
                P, T, h, s, x
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
                unit: DerivativeUnit,
                eval: IF97.() -> Triple<Quantity, Quantity, Double?>): Lazy<Quantity> =
                lazy { type(if97Instance.eval().third ?: NaN, unit) }

        @Suppress("UNCHECKED_CAST")
        private inline fun getTripleFromPropertyPair(arg1: Quantity, arg2: Quantity) = Triple<Quantity, Quantity, Double?>(
                arg1[arg1.property.baseUnit.alias],
                arg2[arg2.property.baseUnit.alias],
                null)

        private fun getTripleOrThrowException(
                s: Quantity?,
                x: Quantity?,
                P: Quantity?,
                T: Quantity?,
                h: Quantity?
        ): Triple<Quantity, Quantity, Double?> {
            var q1: Quantity? = null
            var q2: Quantity? = null
            var quantity: Quantity
            if (s != null) q1 = SpecificEntropy(s.value, s.unit)
            if (x != null) {
                quantity = VapourFraction(x.value, x.unit)
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
            if (q1 == null || q2 == null) throw RuntimeException("There must be exactly 2 parameters")
            return getTripleFromPropertyPair(q1, q2)
        }
    }
}