package ru.lextop.steamcalculator.steam.quantity

class QuantityValue(val quantity: Quantity, val value: Double, val unit: DerivedUnit) {
    constructor(quantity: Quantity, value: Number, unit: DerivedUnit) : this(quantity, value.toDouble(), unit)

    private val basicValue = unit.convertToBasic(value)

    operator fun get(unit: DerivedUnit): QuantityValue =
            if (unit.coherentUnit != quantity.coherentUnit) {
                throw RuntimeException("Incompatible CoherentUnit: ${unit.coherentUnit}")
            } else {
                QuantityValue(quantity, unit.convertFromBasic(basicValue), unit)
            }

    fun copy(value: Number, unit: DerivedUnit): QuantityValue =
            QuantityValue(quantity, value, unit)

    override fun hashCode(): Int {
        var result = quantity.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuantityValue) return false
        if (quantity != other.quantity) return false
        if (basicValue != other.basicValue) return false
        return true
    }

    override fun toString(): String =
            if (value.isNaN())
                "${quantity.symbol}=${String.format("%.4g", value)}"
            else
                "${quantity.symbol}=${String.format("%.4g", value)}[${unit.name}]"

    operator fun plus(other: QuantityValue): QuantityValue =
            if (quantity.coherentUnit != other.quantity.coherentUnit) {
                throw RuntimeException("Incompatible CoherentUnit: ${other.quantity.coherentUnit}")
            } else {
                QuantityValue(quantity.coherentUnit.defaultProperty, basicValue + other.basicValue, quantity.coherentUnit.derived)
            }

    operator fun minus(other: QuantityValue): QuantityValue =
            if (quantity.coherentUnit != other.quantity.coherentUnit) {
                throw RuntimeException("Incompatible CoherentUnit: ${other.quantity.coherentUnit}")
            } else {
                QuantityValue(quantity.coherentUnit.defaultProperty, basicValue - other.basicValue, quantity.coherentUnit.derived)
            }

    operator fun times(other: QuantityValue): QuantityValue {
        val newBaseUnit = quantity.coherentUnit * other.quantity.coherentUnit
        return QuantityValue(newBaseUnit.defaultProperty, basicValue * other.basicValue, newBaseUnit.derived)
    }

    operator fun div(other: QuantityValue): QuantityValue {
        val newBaseUnit = quantity.coherentUnit / other.quantity.coherentUnit
        return QuantityValue(newBaseUnit.defaultProperty, basicValue / other.basicValue, newBaseUnit.derived)
    }
}

operator fun Double.invoke(unit: DerivedUnit) = QuantityValue(unit.coherentUnit.defaultProperty, this, unit)
operator fun Number.invoke(unit: DerivedUnit) = QuantityValue(unit.coherentUnit.defaultProperty, this, unit)
operator fun Quantity.invoke(value: Number, unit: DerivedUnit) = QuantityValue(this, value, unit)
operator fun Quantity.invoke(value: Double, unit: DerivedUnit) = QuantityValue(this, value, unit)