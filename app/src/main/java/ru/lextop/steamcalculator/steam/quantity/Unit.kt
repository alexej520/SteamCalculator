package ru.lextop.steamcalculator.steam.quantity

import java.util.concurrent.ConcurrentHashMap

data class BaseUnit(
        val m: Int = 0,
        val kg: Int = 0,
        val s: Int = 0,
        val A: Int = 0,
        val K: Int = 0,
        val mol: Int = 0,
        val cd: Int = 0
) {
    val defaultProperty = Property(toString(), "", this)
    val alias: DerivativeUnit by lazy {
        (derivativeUnitMap as MutableMap)["alias"]!!
    }
    val derivativeUnitMap: Map<String, DerivativeUnit>

    init {
        derivativeUnitMap = baseUnitMap.getOrPut(this) { ConcurrentHashMap() }
    }

    companion object {
        private val baseUnitMap: MutableMap<BaseUnit, ConcurrentHashMap<String, DerivativeUnit>> = ConcurrentHashMap()
    }

    operator fun div(unit: BaseUnit) = BaseUnit(
            m - unit.m,
            kg - unit.kg,
            s - unit.s,
            A - unit.A,
            K - unit.K,
            mol - unit.mol,
            cd - unit.cd
    )

    operator fun times(unit: BaseUnit) = BaseUnit(
            m + unit.m,
            kg + unit.kg,
            s + unit.s,
            A + unit.A,
            K + unit.K,
            mol + unit.mol,
            cd + unit.cd
    )
}

data class DerivativeUnit(
        val baseUnit: BaseUnit,
        val name: String,
        val factor: Double,
        val addition: Double
) {
    fun convertFromBasic(basicValue: Double): Double = basicValue * factor + addition
    fun convertToBasic(value: Double): Double = (value - addition) / factor
    operator fun times(unit: DerivativeUnit) = DerivativeUnit(
            baseUnit * unit.baseUnit,
            "$name*${unit.name}",
            factor * unit.factor,
            0.0)

    operator fun div(unit: DerivativeUnit) = DerivativeUnit(
            baseUnit / unit.baseUnit,
            "$name/${unit.name}",
            factor / unit.factor,
            0.0)

}

operator fun DerivativeUnit.times(times: Double) =
        copy(factor = factor * times, addition = addition * times)

operator fun Double.times(unit: DerivativeUnit) =
        unit.copy(factor = unit.factor * this, addition = unit.addition * this)

operator fun DerivativeUnit.div(div: Double) =
        copy(factor = factor / div, addition = addition / div)

operator fun Double.div(unit: DerivativeUnit) =
        unit.copy(factor = unit.factor / this, addition = unit.addition / this)

operator fun DerivativeUnit.plus(plus: Double) =
        copy(addition = addition + plus)

operator fun Double.plus(unit: DerivativeUnit) =
        unit.copy(addition = unit.addition + this)

operator fun DerivativeUnit.minus(minus: Double) =
        copy(addition = addition - minus)

operator fun Double.minus(unit: DerivativeUnit) =
        unit.copy(addition = this - unit.addition)

infix fun DerivativeUnit.name(name: String): DerivativeUnit {
    val unitWithNewName = copy(name = name)
    return if ((baseUnit.derivativeUnitMap as MutableMap).put(name, unitWithNewName) != null) {
        throw RuntimeException("$baseUnit already has unitName: $name")
    } else {
        unitWithNewName
    }
}


infix fun BaseUnit.alias(name: String): DerivativeUnit {
    val unit = DerivativeUnit(this, "", 1.0, 0.0) name name
    (derivativeUnitMap as MutableMap).put("alias", unit)
    return unit
}

object byDefault

infix fun DerivativeUnit.name(byDefault: byDefault) =
        this name name

inline fun DerivativeUnit.withPrefix(prefix: String, times: Double) =
        copy(factor = factor * times, name = "$prefix$name")

fun da(unit: DerivativeUnit) = unit.withPrefix("da", 1e-1)
fun h(unit: DerivativeUnit) = unit.withPrefix("h", 1e-2)
fun k(unit: DerivativeUnit) = unit.withPrefix("k", 1e-3)
fun M(unit: DerivativeUnit) = unit.withPrefix("M", 1e-6)
fun G(unit: DerivativeUnit) = unit.withPrefix("G", 1e-9)
fun T(unit: DerivativeUnit) = unit.withPrefix("T", 1e-12)
fun P(unit: DerivativeUnit) = unit.withPrefix("P", 1e-15)
fun E(unit: DerivativeUnit) = unit.withPrefix("E", 1e-18)
fun Z(unit: DerivativeUnit) = unit.withPrefix("Z", 1e-21)
fun Y(unit: DerivativeUnit) = unit.withPrefix("Y", 1e-24)

fun d(unit: DerivativeUnit) = unit.withPrefix("d", 1e1)
fun c(unit: DerivativeUnit) = unit.withPrefix("c", 1e2)
fun m(unit: DerivativeUnit) = unit.withPrefix("m", 1e3)
fun mc(unit: DerivativeUnit) = unit.withPrefix("µ", 1e6)
fun n(unit: DerivativeUnit) = unit.withPrefix("n", 1e9)
fun p(unit: DerivativeUnit) = unit.withPrefix("p", 1e12)
fun f(unit: DerivativeUnit) = unit.withPrefix("f", 1e15)
fun a(unit: DerivativeUnit) = unit.withPrefix("a", 1e18)
fun z(unit: DerivativeUnit) = unit.withPrefix("z", 1e21)
fun y(unit: DerivativeUnit) = unit.withPrefix("y", 1e24)