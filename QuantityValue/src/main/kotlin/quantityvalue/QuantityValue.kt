package quantityvalue

import java.util.Locale

data class QuantityValue(val quantity: Quantity, val value: Double, val unit: UnitPh) {
    constructor(quantity: Quantity, value: Number, unit: UnitPh) : this(quantity, value.toDouble(), unit)

    init {
        if (quantity.dimension != unit.dimension)
            throw RuntimeException("Incompatible Dimension: ${unit.dimension}")
    }

    internal val coherentValue = unit.convertToCoherent(value)

    operator fun get(unit: UnitPh): QuantityValue =
            if (unit.dimension != quantity.dimension) {
                throw RuntimeException("Incompatible Dimension: ${unit.dimension}")
            } else {
                QuantityValue(quantity, unit.convertFromCoherent(coherentValue), unit)
            }

    override fun toString(): String =
            if (value.isNaN())
                "${quantity.symbol}=${String.format(Locale.ROOT, "%.4g", value)}"
            else
                "${quantity.symbol}=${String.format(Locale.ROOT, "%.4g", value)}[${unit.symbol}]"

    operator fun plus(other: QuantityValue): QuantityValue =
            if (quantity.dimension != other.quantity.dimension) {
                throw RuntimeException("Incompatible Dimension: ${other.quantity.dimension}")
            } else {
                QuantityValue(
                        quantity = Quantity(dimension = quantity.dimension),
                        value = coherentValue + other.coherentValue,
                        unit = CoherentUnit(quantity.dimension))
            }

    operator fun minus(other: QuantityValue): QuantityValue =
            if (quantity.dimension != other.quantity.dimension) {
                throw RuntimeException("Incompatible Dimension: ${other.quantity.dimension}")
            } else {
                QuantityValue(
                        quantity = Quantity(dimension = quantity.dimension),
                        value = coherentValue - other.coherentValue,
                        unit = CoherentUnit(quantity.dimension))
            }

    operator fun times(other: QuantityValue): QuantityValue {
        val newDimension = quantity.dimension * other.quantity.dimension
        return QuantityValue(
                quantity = Quantity(dimension = newDimension),
                value = coherentValue * other.coherentValue,
                unit = CoherentUnit(newDimension))
    }

    operator fun div(other: QuantityValue): QuantityValue {
        val newDimension = quantity.dimension * other.quantity.dimension
        return QuantityValue(
                quantity = Quantity(dimension = newDimension),
                value = coherentValue / other.coherentValue,
                unit = CoherentUnit(newDimension))
    }
}

operator fun Double.invoke(unit: UnitPh) = QuantityValue(Quantity(dimension = unit.dimension), this, unit)
operator fun Number.invoke(unit: UnitPh) = QuantityValue(Quantity(dimension = unit.dimension), this, unit)
operator fun Quantity.invoke(value: Number, unit: UnitPh) = QuantityValue(this, value, unit)
operator fun Quantity.invoke(value: Double, unit: UnitPh) = QuantityValue(this, value, unit)