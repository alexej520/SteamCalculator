package ru.lextop.steamcalculator.steam.quantity

class QuantityValue(val derivedQuantity: DerivedQuantity, val value: Double, val unit: DerivedUnit) {
    constructor(derivedQuantity: DerivedQuantity, value: Number, unit: DerivedUnit) : this(derivedQuantity, value.toDouble(), unit)

    private val basicValue = unit.convertToBasic(value)

    operator fun get(unit: DerivedUnit): QuantityValue =
            if (unit.coherentUnit != derivedQuantity.coherentUnit) {
                throw RuntimeException("Incompatible CoherentUnit: ${unit.coherentUnit}")
            } else {
                QuantityValue(derivedQuantity, unit.convertFromBasic(basicValue), unit)
            }

    fun copy(value: Number, unit: DerivedUnit): QuantityValue =
            QuantityValue(derivedQuantity, value, unit)

    override fun hashCode(): Int {
        var result = derivedQuantity.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuantityValue) return false
        if (derivedQuantity != other.derivedQuantity) return false
        if (basicValue != other.basicValue) return false
        return true
    }

    override fun toString(): String =
            if (value.isNaN())
                "${derivedQuantity.symbol}=${String.format("%.4g", value)}"
            else
                "${derivedQuantity.symbol}=${String.format("%.4g", value)}[${unit.name}]"

    operator fun plus(other: QuantityValue): QuantityValue =
            if (derivedQuantity.coherentUnit != other.derivedQuantity.coherentUnit) {
                throw RuntimeException("Incompatible CoherentUnit: ${other.derivedQuantity.coherentUnit}")
            } else {
                QuantityValue(derivedQuantity.coherentUnit.defaultProperty, basicValue + other.basicValue, derivedQuantity.coherentUnit.derived)
            }

    operator fun minus(other: QuantityValue): QuantityValue =
            if (derivedQuantity.coherentUnit != other.derivedQuantity.coherentUnit) {
                throw RuntimeException("Incompatible CoherentUnit: ${other.derivedQuantity.coherentUnit}")
            } else {
                QuantityValue(derivedQuantity.coherentUnit.defaultProperty, basicValue - other.basicValue, derivedQuantity.coherentUnit.derived)
            }

    operator fun times(other: QuantityValue): QuantityValue {
        val newBaseUnit = derivedQuantity.coherentUnit * other.derivedQuantity.coherentUnit
        return QuantityValue(newBaseUnit.defaultProperty, basicValue * other.basicValue, newBaseUnit.derived)
    }

    operator fun div(other: QuantityValue): QuantityValue {
        val newBaseUnit = derivedQuantity.coherentUnit / other.derivedQuantity.coherentUnit
        return QuantityValue(newBaseUnit.defaultProperty, basicValue / other.basicValue, newBaseUnit.derived)
    }
}

operator fun Double.invoke(unit: DerivedUnit) = QuantityValue(unit.coherentUnit.defaultProperty, this, unit)
operator fun Number.invoke(unit: DerivedUnit) = QuantityValue(unit.coherentUnit.defaultProperty, this, unit)