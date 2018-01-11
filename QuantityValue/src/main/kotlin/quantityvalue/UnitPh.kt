package quantityvalue

import java.util.*

data class UnitPh(
        val dimension: Dimension,
        val factor: Double = Double.NaN,
        val name: String = "AnonymousUnit",
        val symbol: String = defaultSymbol(factor, dimension),
        val converter: Converter? = null) {
    fun convertToCoherent(value: Double): Double =
            converter?.convertToCoherent(value) ?: value / factor

    fun convertFromCoherent(value: Double): Double =
            converter?.convertFromCoherent(value) ?: value * factor

    interface Converter {
        fun convertToCoherent(value: Double): Double
        fun convertFromCoherent(value: Double): Double
    }

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

// Use with Temperature Units for example (C, F, etc.)

class OffsetUnitConverter(
        val factor: Double,
        val offset: Double)
    : UnitPh.Converter {
    override fun convertToCoherent(value: Double): Double = (value - offset) / factor
    override fun convertFromCoherent(value: Double): Double = value * factor + offset
}

@Suppress("NOTHING_TO_INLINE")
inline fun CoherentUnit(
        dimension: Dimension,
        name: String = "AnonymousUnit",
        symbol: String = UnitPh.defaultSymbol(1.0, dimension))
        : UnitPh = UnitPh(dimension, factor = 1.0, name = name, symbol = symbol)

@Suppress("NOTHING_TO_INLINE")
private inline fun UnitPh.checkWithoutConverter() {
    if (converter != null) {
        throw UnsupportedOperationException("unit must have converter == null")
    }
}

operator fun UnitPh.times(unit: UnitPh): UnitPh =
        UnitPh(dimension = dimension * unit.dimension, factor = factor * unit.factor)

operator fun UnitPh.div(unit: UnitPh): UnitPh =
        UnitPh(dimension = dimension / unit.dimension, factor = factor / unit.factor)

operator fun UnitPh.times(times: Double): UnitPh =
        UnitPh(dimension = dimension, factor = factor * times)

operator fun Double.times(unit: UnitPh): UnitPh =
        UnitPh(dimension = unit.dimension, factor = unit.factor * this)

operator fun UnitPh.div(div: Double): UnitPh =
        UnitPh(dimension = dimension, factor = factor / div)

operator fun Double.div(unit: UnitPh): UnitPh =
        UnitPh(dimension = unit.dimension, factor = unit.factor / this)

@Suppress("NOTHING_TO_INLINE")
private inline fun UnitPh.withPrefix(prefix: String, times: Double): UnitPh {
    checkWithoutConverter()
    return UnitPh(dimension = dimension, factor = factor * times)
}

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
        UnitPh(dimension = dimension, symbol = symbol, name = name, factor = factor, converter = converter)

infix fun UnitPh.withName(name: String): UnitPh =
        UnitPh(dimension = dimension, symbol = symbol, name = name, factor = factor, converter = converter)
