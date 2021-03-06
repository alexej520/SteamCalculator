package quantityvalue

import java.util.Locale

data class QuantityValue(val quantity: Quantity, val value: Double, val unit: UnitPh) {
    var coherentValue = Double.NaN
    init {
        if (quantity.dimension != unit.dimension)
            throw IllegalArgumentException("Incompatible Dimension: ${unit.dimension}")
       coherentValue = unit.convertToCoherent(value)
    }
    constructor(quantity: Quantity, value: Number, unitConverter: UnitConverter) : this(quantity, value.toDouble(), unitConverter.unit){
        coherentValue = unitConverter.convertToCoherent(value.toDouble())
    }

/*

    operator fun get(unit: UnitPh): QuantityValue =
            if (unit.dimension != quantity.dimension) {
                throw IllegalArgumentException("Incompatible Dimension: ${unit.dimension}")
            } else {
                QuantityValue(quantity, unit.convertFromCoherent(coherentValue), unit)
            }
*/
    operator fun get(converter: UnitConverter): QuantityValue {
        if (converter.unit.dimension != quantity.dimension) {
            throw IllegalArgumentException("Incompatible Dimension: ${unit.dimension}")
        }
        return QuantityValue(quantity, converter.convertFromCoherent(coherentValue), converter.unit)
    }

    fun equals(other: QuantityValue, ignoreUnit: Boolean = false, ignoreQuantity: Boolean = false,
               ignoreUnitSymbol: Boolean = false, ignoreUnitName: Boolean = false,
               ignoreQuantitySymbol: Boolean = false, ignoreQuantityName: Boolean = false): Boolean {
        if (this === other) return true

        if (coherentValue != other.coherentValue) return false
        if (!ignoreUnit && !unit.equals(other.unit, ignoreName = ignoreUnitName, ignoreSymbol = ignoreUnitSymbol)) return false
        if (!ignoreQuantity && quantity.equals(other.quantity, ignoreName = ignoreQuantityName, ignoreSymbol = ignoreQuantitySymbol)) return false

        return true
    }

    override fun toString(): String =
            if (value.isNaN())
                "${quantity.symbol}=${String.format(Locale.ROOT, "%.4g", value)}"
            else
                "${quantity.symbol}=${String.format(Locale.ROOT, "%.4g", value)}[${unit.symbol}]"

    operator fun plus(other: QuantityValue): QuantityValue =
            if (quantity.dimension != other.quantity.dimension) {
                throw IllegalArgumentException("Incompatible Dimension: ${other.quantity.dimension}")
            } else {
                QuantityValue(
                        quantity = Quantity(dimension = quantity.dimension),
                        value = coherentValue + other.coherentValue,
                        unit = CoherentUnit(quantity.dimension))
            }

    operator fun minus(other: QuantityValue): QuantityValue =
            if (quantity.dimension != other.quantity.dimension) {
                throw IllegalArgumentException("Incompatible Dimension: ${other.quantity.dimension}")
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