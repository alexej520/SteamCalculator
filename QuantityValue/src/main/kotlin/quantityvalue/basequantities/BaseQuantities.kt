package quantityvalue.basequantities

import quantityvalue.*
import quantityvalue.baseunits.*

internal fun BaseQuantity(name: String, symbol: String, baseUnit: UnitPh): Quantity {
    if (!checkBaseDimension(baseUnit.dimension))
        throw IllegalArgumentException("BaseUnit must have only one dimension that == 1, other dimensions must be == 0")
    return Quantity(name, symbol, baseUnit.dimension)
}

val Mass = BaseQuantity("Mass", "L", kg)
val Length = BaseQuantity("Length", "l", m)
val Time = BaseQuantity("Time", "t", s)
val ElectricCurrent = BaseQuantity("Electric Current", "I", A)
val Temperature = BaseQuantity("Temperature", "T", K)
val AmountOfSubstance = BaseQuantity("Amount Of Substance", "n", mol)
val LuminousIntensity = BaseQuantity("Luminous Intensity", "Iv", cd)
