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
}

open class DerivedUnt internal constructor(
        dimension: Dimension,
        name: String? = null,
        symbol: String,
        factor: Double = Double.NaN)
    : UnitPh(
        dimension = dimension,
        name = name ?: "AnonymousDerivedUnit",
        symbol = symbol,
        factor = factor) {
    constructor(
            coherentUnit: CoherentUnt,
            name: String? = null,
            symbol: String? = null,
            factor: Double = Double.NaN)
            : this(
            dimension = coherentUnit.dimension,
            name = name,
            symbol = symbol ?: defaultSymbol(factor, coherentUnit),
            factor = factor){
        _coherent = coherentUnit
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
                if (m != 0) append("m^").append(m)
                if (kg != 0) append("kg^").append(kg)
                if (s != 0) append("s^").append(s)
                if (A != 0) append("A^").append(A)
                if (K != 0) append("K^").append(K)
                if (mol != 0) append("mol^").append(mol)
                if (cd != 0) append("cd^").append(cd)
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
                if (m == 1) dimCounter++
                else if (m != 0) return false
                if (kg == 1) dimCounter++
                else if (kg != 0) return false
                if (s == 1) dimCounter++
                else if (s != 0) return false
                if (A == 1) dimCounter++
                else if (A != 0) return false
                if (K == 1) dimCounter++
                else if (K != 0) return false
                if (mol == 1) dimCounter++
                else if (mol != 0) return false
                if (cd == 1) dimCounter++
                else if (cd != 0) return false
                if (dimCounter == 1) return true
                return false
            }
        }
    }
}

operator fun UnitPh.times(unit: UnitPh): UnitPh =
        DerivedUnt(coherentUnit = CoherentUnt(dimension * unit.dimension), factor = factor * unit.factor)

operator fun UnitPh.div(unit: UnitPh): UnitPh =
        DerivedUnt(coherentUnit = CoherentUnt(dimension / unit.dimension), factor = factor / unit.factor)

infix fun UnitPh.withSymbol(symbol: String): UnitPh =
    DerivedUnt(coherentUnit = coherent, symbol = symbol, factor = factor)

infix fun UnitPh.withName(nameToSymbol: Pair<String, String>): UnitPh =
        DerivedUnt(coherentUnit = coherent, name = nameToSymbol.first, symbol = nameToSymbol.second, factor = factor)
