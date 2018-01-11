package quantityvalue.baseunits

import quantityvalue.*


internal fun BaseUnit(dimension: Dimension, name: String, symbol: String): UnitPh {
    if (!checkBaseDimension(dimension))
        throw IllegalArgumentException("BaseUnit must have only one dimension that == 1, other dimensions must be == 0")
    return CoherentUnit(dimension, name, symbol)
}

val m = BaseUnit(Dimension(L = 1), "Metre", "m")
val kg = BaseUnit(Dimension(M = 1), "Kilogram", "kg")
val s = BaseUnit(Dimension(T = 1), "Second", "s")
val A = BaseUnit(Dimension(I = 1), "Ampere", "A")
val K = BaseUnit(Dimension(O = 1), "Kelvin", "K")
val mol = BaseUnit(Dimension(N = 1), "Molar", "mol")
val cd = BaseUnit(Dimension(J = 1), "Candela", "cd")
