package ru.lextop.steamcalculator.steam.quantity

abstract class BaseUnit private constructor(private val id: List<Int>) {
    val unitList: List<UnitPh> = mutableListOf()
    val unitMap: Map<String, UnitPh> = mutableMapOf()
    val defaultProperty = Property(toString(), "", this, 0, 0)
    private var _alias: UnitPh? = null
    val alias: UnitPh get() = _alias!!

    operator fun div(unit: BaseUnit): BaseUnit {
        val resultId = id.zip(unit.id).map { (i1, i2) -> i1 - i2 }
        return baseUnits.getOrPut(resultId) { object : BaseUnit(resultId){} }
    }

    operator fun times(unit: BaseUnit): BaseUnit {
        val resultId = id.zip(unit.id).map { (i1, i2) -> i1 + i2 }
        return baseUnits.getOrPut(resultId) { object : BaseUnit(resultId){} }
    }

    protected constructor(m: Int = 0,
                          kg: Int = 0,
                          s: Int = 0,
                          A: Int = 0,
                          K: Int = 0,
                          mol: Int = 0,
                          cd: Int = 0) : this(listOf(m, kg, s, A, K, mol, cd))

    companion object {
        private val baseUnits: MutableMap<List<Int>, BaseUnit> = mutableMapOf()
    }

    override fun toString(): String {
        return id.joinToString(prefix = "BaseUnit[", separator = ",", postfix = "]")
    }

    protected infix fun UnitPh.addWith(pair: Pair<String, Int>): UnitPh {
        if (this@BaseUnit != baseUnit) throw RuntimeException("Incorporable BaseUnit: $baseUnit")
        val unitWithNewName = copy(name = pair.first, id = pair.second)
        return if ((unitMap as MutableMap).put(unitWithNewName.name, unitWithNewName) != null) {
            throw RuntimeException("$baseUnit already has unitName: $name")
        } else {
            (unitList as MutableList).add(unitWithNewName)
            unitWithNewName
        }
    }

    protected object byDefault

    @JvmName("addWithDefaultName")
    protected infix fun UnitPh.addWith(pair: Pair<byDefault, Int>) =
            this addWith (name to pair.second)

    protected fun createAlias(pair: Pair<String, Int>? = null): UnitPh {
        val unit = UnitPh(this, toString(), 1.0, 0.0, 0)
        val alias = if (pair == null) unit else unit addWith pair
        _alias = alias
        if (baseUnits.put(id, this) != null) {
            throw RuntimeException("BaseUnit with same parameters already created")
        }
        return alias
    }
}

data class UnitPh(
        val baseUnit: BaseUnit,
        val name: String,
        val factor: Double,
        val addition: Double,
        val id: Int
) {
    fun convertFromBasic(basicValue: Double): Double = basicValue * factor + addition
    fun convertToBasic(value: Double): Double = (value - addition) / factor
    operator fun times(unit: UnitPh) = UnitPh(
            baseUnit * unit.baseUnit,
            "$name*${unit.name}",
            factor * unit.factor,
            0.0, 0)

    operator fun div(unit: UnitPh) = UnitPh(
            baseUnit / unit.baseUnit,
            "$name/${unit.name}",
            factor / unit.factor,
            0.0, 0)

}

operator fun UnitPh.times(times: Double) =
        copy(factor = factor * times, addition = addition * times)

operator fun Double.times(unit: UnitPh) =
        unit.copy(factor = unit.factor * this, addition = unit.addition * this)

operator fun UnitPh.div(div: Double) =
        copy(factor = factor / div, addition = addition / div)

operator fun Double.div(unit: UnitPh) =
        unit.copy(factor = unit.factor / this, addition = unit.addition / this)

operator fun UnitPh.plus(plus: Double) =
        copy(addition = addition + plus)

operator fun Double.plus(unit: UnitPh) =
        unit.copy(addition = unit.addition + this)

operator fun UnitPh.minus(minus: Double) =
        copy(addition = addition - minus)

operator fun Double.minus(unit: UnitPh) =
        unit.copy(addition = this - unit.addition)

inline fun UnitPh.withPrefix(prefix: String, times: Double) =
        copy(factor = factor * times, name = "$prefix$name")

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
fun m(unit: UnitPh) = unit.withPrefix("m", 1e3)
fun mc(unit: UnitPh) = unit.withPrefix("µ", 1e6)
fun n(unit: UnitPh) = unit.withPrefix("n", 1e9)
fun p(unit: UnitPh) = unit.withPrefix("p", 1e12)
fun f(unit: UnitPh) = unit.withPrefix("f", 1e15)
fun a(unit: UnitPh) = unit.withPrefix("a", 1e18)
fun z(unit: UnitPh) = unit.withPrefix("z", 1e21)
fun y(unit: UnitPh) = unit.withPrefix("y", 1e24)