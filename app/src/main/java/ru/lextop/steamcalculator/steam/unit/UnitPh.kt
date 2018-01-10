package ru.lextop.steamcalculator.steam.unit

import ru.lextop.steamcalculator.steam.quantity.Dimension
import java.util.*

abstract class UnitPh internal constructor(
        val dimension: Dimension,
        val name: String,
        val symbol: String,
        val factor: Double
) {
    abstract val coherent: CoherentUnt
    abstract fun convertToCoherent(value: Double): Double
    abstract fun convertFromCoherent(value: Double): Double

    companion object {
        val UNDEFINED = Double.NaN
    }
}

open class DerivedUnt internal constructor(
        dimension: Dimension,
        name: String? = null,
        symbol: String,
        factor: Double)
    : UnitPh(
        dimension = dimension,
        name = name ?: "AnonymousDerivedUnit",
        symbol = symbol,
        factor = factor) {
    constructor(
            coherent: CoherentUnt,
            name: String? = null,
            symbol: String? = null,
            factor: Double = Double.NaN)
            : this(
            dimension = coherent.dimension,
            name = name,
            symbol = symbol ?: defaultSymbol(factor, coherent),
            factor = factor) {
        _coherent = coherent
    }

    private var _coherent: CoherentUnt? = null
    override val coherent: CoherentUnt get() = _coherent!!
    override fun convertToCoherent(value: Double) = factor / value
    override fun convertFromCoherent(value: Double) = factor * value

    companion object {
        fun defaultSymbol(factor: Double, coherentUnit: CoherentUnt): String =
                if (factor != 1.0) "%.4g".format(Locale.ROOT, factor) + coherentUnit.toString() else coherentUnit.toString()
    }
}

// for Temperature units

class OffsetDerivedUnit(
        coherent: CoherentUnt,
        name: String? = null,
        symbol: String? = null,
        factor: Double,
        val offset: Double)
    : DerivedUnt(
        coherent = coherent,
        name = name,
        symbol = symbol ?: defaultSymbol(factor, coherent),
        factor = factor) {
    override fun convertToCoherent(value: Double): Double = (value - offset) / factor
    override fun convertFromCoherent(value: Double): Double = value * factor + offset
}

open class CoherentUnt internal constructor(
        dimension: Dimension,
        name: String? = null,
        symbol: String? = null
) : DerivedUnt(
        dimension = dimension,
        name = name ?: "AnonymousCoherentUnit",
        symbol = symbol ?: defaultSymbol(1.0, dimension),
        factor = 1.0) {
    override val coherent: CoherentUnt get() = this

    companion object {
        fun defaultSymbol(factor: Double, dimension: Dimension): String = with(dimension) {
            StringBuilder().apply {
                if (L != 0) append("m").append(L)
                if (M != 0) append("kg").append(M)
                if (T != 0) append("s").append(T)
                if (I != 0) append("A").append(I)
                if (O != 0) append("T").append(O)
                if (N != 0) append("mol").append(N)
                if (J != 0) append("cd").append(J)
                if (length != 0 && factor != 1.0) {
                    insert(0, "%.4g".format(Locale.ROOT, factor))
                }
            }.toString()
        }
    }
}

fun CoherentUnit(dimension: Dimension, name: String? = null, symbol: String? = null) =
        CoherentUnt(dimension, name, symbol)

class BaseUnit internal constructor(
        dimension: Dimension,
        name: String,
        symbol: String
) : CoherentUnt(dimension, name, symbol) {
    init {
        if (!checkDimension(dimension))
            throw IllegalArgumentException("BaseUnit must have only one dimension that == 1, other dimensions must be == 0")
    }

    companion object {
        private fun checkDimension(dimension: Dimension): Boolean {
            with(dimension) {
                var dimCounter = 0
                if (L == 1) dimCounter++
                else if (L != 0) return false
                if (M == 1) dimCounter++
                else if (M != 0) return false
                if (T == 1) dimCounter++
                else if (T != 0) return false
                if (I == 1) dimCounter++
                else if (I != 0) return false
                if (O == 1) dimCounter++
                else if (O != 0) return false
                if (N == 1) dimCounter++
                else if (N != 0) return false
                if (J == 1) dimCounter++
                else if (J != 0) return false
                if (dimCounter == 1) return true
                return false
            }
        }
    }
}

operator fun UnitPh.times(unit: UnitPh): UnitPh =
        DerivedUnt(coherent = CoherentUnt(dimension * unit.dimension), factor = factor * unit.factor)

operator fun UnitPh.div(unit: UnitPh): UnitPh =
        DerivedUnt(coherent = CoherentUnt(dimension / unit.dimension), factor = factor / unit.factor)

operator fun UnitPh.times(times: Double): UnitPh =
        DerivedUnt(coherent = coherent, factor = factor * times)

operator fun Double.times(unit: UnitPh): UnitPh =
        DerivedUnt(coherent = unit.coherent, factor = unit.factor * this)

operator fun UnitPh.div(div: Double): UnitPh =
        DerivedUnt(coherent = coherent, factor = factor / div)

operator fun Double.div(unit: UnitPh): UnitPh =
        DerivedUnt(coherent = unit.coherent, factor = unit.factor / this)

operator fun UnitPh.plus(plus: Double): UnitPh =
        OffsetDerivedUnit(coherent = coherent, factor = factor, offset = ((this as? OffsetDerivedUnit)?.offset ?: 0.0) + plus)

operator fun Double.plus(unit: UnitPh): UnitPh =
        OffsetDerivedUnit(coherent = unit.coherent, factor = unit.factor, offset = ((unit as? OffsetDerivedUnit)?.offset ?: 0.0) + this)

operator fun DerivedUnt.minus(minus: Double) =
        OffsetDerivedUnit(coherent = coherent, factor = factor, offset = ((this as? OffsetDerivedUnit)?.offset ?: 0.0) - minus)

operator fun Double.minus(unit: UnitPh): UnitPh =
        OffsetDerivedUnit(coherent = unit.coherent, factor = unit.factor, offset = this - ((unit as? OffsetDerivedUnit)?.offset ?: 0.0))

@Suppress("NOTHING_TO_INLINE")
private inline fun UnitPh.withPrefix(prefix: String, times: Double): UnitPh =
        DerivedUnt(coherent = coherent, factor = factor * times)

fun da(unit: UnitPh) = unit.withPrefix("da", 1e-1)
fun h(unit: UnitPh) = unit.withPrefix("h", 1e-2)
fun k(unit: UnitPh) = unit.withPrefix("k", 1e-3)
fun M(unit: UnitPh) = unit.withPrefix("M", 1e-6)
fun G(unit: UnitPh) = unit.withPrefix("G", 1e-9)
fun T(unit: UnitPh) = unit.withPrefix("T", 1e-12)
fun P(unit: UnitPh) = unit.withPrefix("P", 1e-15)
fun E(unit: UnitPh) = unit.withPrefix("E", 1e-18)
fun Z(unit: UnitPh) = unit.withPrefix("Z", 1e-21)
fun Y(unit: UnitPh) = unit.withPrefix("Y", 1e-24)

fun d(unit: UnitPh) = unit.withPrefix("d", 1e1)
fun c(unit: UnitPh) = unit.withPrefix("c", 1e2)
fun m(unit: UnitPh) = unit.withPrefix("L", 1e3)
fun mc(unit: UnitPh) = unit.withPrefix("µ", 1e6)
fun n(unit: UnitPh) = unit.withPrefix("n", 1e9)
fun p(unit: UnitPh) = unit.withPrefix("p", 1e12)
fun f(unit: UnitPh) = unit.withPrefix("f", 1e15)
fun a(unit: UnitPh) = unit.withPrefix("a", 1e18)
fun z(unit: UnitPh) = unit.withPrefix("z", 1e21)
fun y(unit: UnitPh) = unit.withPrefix("y", 1e24)

infix fun UnitPh.withSymbol(symbol: String): UnitPh =
        DerivedUnt(coherent = coherent, symbol = symbol, name = name, factor = factor)

infix fun UnitPh.withName(name: String): UnitPh =
        DerivedUnt(coherent = coherent, symbol = symbol, name = name, factor = factor)
