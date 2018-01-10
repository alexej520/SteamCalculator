package ru.lextop.steamcalculator.steam.quantity

open class CoherentUnit private constructor(val dimension: Dimension) {
    constructor(m: Int = 0,
                kg: Int = 0,
                s: Int = 0,
                A: Int = 0,
                K: Int = 0,
                mol: Int = 0,
                cd: Int = 0) : this(Dimension(m, kg, s, A, K, mol, cd))
    val derivedUnits: List<DerivedUnit> = mutableListOf()
    val derivedUnitMap: Map<String, DerivedUnit> = mutableMapOf()
    val defaultProperty = DerivedQuantity(toString(), "", this, 0, 0)
    private var _derived: DerivedUnit? = null
    val derived: DerivedUnit get() = _derived!!

    operator fun div(unit: CoherentUnit): CoherentUnit {
        val resultDim = dimension / unit.dimension
        return coherentUnits.getOrPut(resultDim) { CoherentUnit(resultDim) }
    }

    operator fun times(unit: CoherentUnit): CoherentUnit {
        val resultDim = dimension * unit.dimension
        return coherentUnits.getOrPut(resultDim) { CoherentUnit(resultDim) }
    }

    companion object {
        private val coherentUnits: MutableMap<Dimension, CoherentUnit> = mutableMapOf()
    }

    override fun toString(): String {
        return with(dimension) { "CoherentUnit[L = $L, M = $M, T = $T, A = $A, K = $K, mol = $mol, cd = $cd]" }
    }

    protected infix fun DerivedUnit.addWith(pair: Pair<String, Int>): DerivedUnit {
        if (this@CoherentUnit != coherentUnit) throw RuntimeException("Incorporable Unit: ${coherentUnit}")
        val unitWithNewName = copy(name = pair.first, id = pair.second)
        return if ((derivedUnitMap as MutableMap).put(unitWithNewName.name, unitWithNewName) != null) {
            throw RuntimeException("${coherentUnit} already has unitName: $name")
        } else {
            (derivedUnits as MutableList).add(unitWithNewName)
            unitWithNewName
        }
    }

    protected object byDefault

    @JvmName("addWithDefaultName")
    protected infix fun DerivedUnit.addWith(pair: Pair<byDefault, Int>) =
            this addWith (name to pair.second)

    protected fun createCoherentUnit(pair: Pair<String, Int>? = null): DerivedUnit {
        val unit = DerivedUnit(this, toString(), 1.0, 0.0, 0)
        val alias = if (pair == null) unit else unit addWith pair
        _derived = alias
        if (coherentUnits.put(dimension, this) != null) {
            throw RuntimeException("CoherentUnit with same parameters already created")
        }
        return alias
    }
}

data class DerivedUnit(
        val coherentUnit: CoherentUnit,
        val name: String,
        val factor: Double,
        val addition: Double,
        val id: Int
) {
    fun convertFromBasic(basicValue: Double): Double = basicValue * factor + addition
    fun convertToBasic(value: Double): Double = (value - addition) / factor
    operator fun times(unit: DerivedUnit) = DerivedUnit(
            coherentUnit * unit.coherentUnit,
            "$name*${unit.name}",
            factor * unit.factor,
            0.0, 0)

    operator fun div(unit: DerivedUnit) = DerivedUnit(
            coherentUnit / unit.coherentUnit,
            "$name/${unit.name}",
            factor / unit.factor,
            0.0, 0)

}

operator fun DerivedUnit.times(times: Double) =
        copy(factor = factor * times, addition = addition * times)

operator fun Double.times(unit: DerivedUnit) =
        unit.copy(factor = unit.factor * this, addition = unit.addition * this)

operator fun DerivedUnit.div(div: Double) =
        copy(factor = factor / div, addition = addition / div)

operator fun Double.div(unit: DerivedUnit) =
        unit.copy(factor = unit.factor / this, addition = unit.addition / this)

operator fun DerivedUnit.plus(plus: Double) =
        copy(addition = addition + plus)

operator fun Double.plus(unit: DerivedUnit) =
        unit.copy(addition = unit.addition + this)

operator fun DerivedUnit.minus(minus: Double) =
        copy(addition = addition - minus)

operator fun Double.minus(unit: DerivedUnit) =
        unit.copy(addition = this - unit.addition)

inline fun DerivedUnit.withPrefix(prefix: String, times: Double) =
        copy(factor = factor * times, name = "$prefix$name")

fun da(unit: DerivedUnit) = unit.withPrefix("da", 1e-1)
fun h(unit: DerivedUnit) = unit.withPrefix("h", 1e-2)
fun k(unit: DerivedUnit) = unit.withPrefix("k", 1e-3)
fun M(unit: DerivedUnit) = unit.withPrefix("M", 1e-6)
fun G(unit: DerivedUnit) = unit.withPrefix("G", 1e-9)
fun T(unit: DerivedUnit) = unit.withPrefix("T", 1e-12)
fun P(unit: DerivedUnit) = unit.withPrefix("P", 1e-15)
fun E(unit: DerivedUnit) = unit.withPrefix("E", 1e-18)
fun Z(unit: DerivedUnit) = unit.withPrefix("Z", 1e-21)
fun Y(unit: DerivedUnit) = unit.withPrefix("Y", 1e-24)

fun d(unit: DerivedUnit) = unit.withPrefix("d", 1e1)
fun c(unit: DerivedUnit) = unit.withPrefix("c", 1e2)
fun m(unit: DerivedUnit) = unit.withPrefix("L", 1e3)
fun mc(unit: DerivedUnit) = unit.withPrefix("µ", 1e6)
fun n(unit: DerivedUnit) = unit.withPrefix("n", 1e9)
fun p(unit: DerivedUnit) = unit.withPrefix("p", 1e12)
fun f(unit: DerivedUnit) = unit.withPrefix("f", 1e15)
fun a(unit: DerivedUnit) = unit.withPrefix("a", 1e18)
fun z(unit: DerivedUnit) = unit.withPrefix("z", 1e21)
fun y(unit: DerivedUnit) = unit.withPrefix("y", 1e24)