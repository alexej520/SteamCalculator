package ru.lextop.steamcalculator.steam.quantity

class Quantity(val property: Property, val value: Double, val unit: UnitPh) {
    constructor(property: Property, value: Number, unit: UnitPh) : this(property, value.toDouble(), unit)

    private val basicValue = unit.convertToBasic(value)

    operator fun get(unit: UnitPh): Quantity =
            if (unit.baseUnit != property.baseUnit) {
                throw RuntimeException("Incompatible BaseUnit: ${unit.baseUnit}")
            } else {
                Quantity(property, unit.convertFromBasic(basicValue), unit)
            }

    fun copy(value: Number, unit: UnitPh): Quantity =
            Quantity(property, value, unit)

    override fun hashCode(): Int {
        var result = property.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quantity) return false
        if (property != other.property) return false
        if (basicValue != other.basicValue) return false
        return true
    }

    override fun toString(): String =
            if (value.isNaN())
                "${property.symbol}=${String.format("%.4g", value)}"
            else
                "${property.symbol}=${String.format("%.4g", value)}[${unit.name}]"

    operator fun plus(other: Quantity): Quantity =
            if (property.baseUnit != other.property.baseUnit) {
                throw RuntimeException("Incompatible BaseUnit: ${other.property.baseUnit}")
            } else {
                Quantity(property.baseUnit.defaultProperty, basicValue + other.basicValue, property.baseUnit.alias)
            }

    operator fun minus(other: Quantity): Quantity =
            if (property.baseUnit != other.property.baseUnit) {
                throw RuntimeException("Incompatible BaseUnit: ${other.property.baseUnit}")
            } else {
                Quantity(property.baseUnit.defaultProperty, basicValue - other.basicValue, property.baseUnit.alias)
            }

    operator fun times(other: Quantity): Quantity {
        val newBaseUnit = property.baseUnit * other.property.baseUnit
        return Quantity(newBaseUnit.defaultProperty, basicValue * other.basicValue, newBaseUnit.alias)
    }

    operator fun div(other: Quantity): Quantity {
        val newBaseUnit = property.baseUnit / other.property.baseUnit
        return Quantity(newBaseUnit.defaultProperty, basicValue / other.basicValue, newBaseUnit.alias)
    }
}

operator fun Double.invoke(unit: UnitPh) = Quantity(unit.baseUnit.defaultProperty, this, unit)
operator fun Number.invoke(unit: UnitPh) = Quantity(unit.baseUnit.defaultProperty, this, unit)