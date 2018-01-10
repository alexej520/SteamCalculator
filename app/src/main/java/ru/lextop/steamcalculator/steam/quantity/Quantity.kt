package ru.lextop.steamcalculator.steam.quantity

abstract class Quantity(
        val name: String,
        val symbol: String,
        val coherentUnit: CoherentUnit,
        val nameId: Int,
        val symbolId: Int) {

    override fun toString(): String = name
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quantity) return false

        if (name != other.name) return false
        if (symbol != other.symbol) return false
        if (coherentUnit != other.coherentUnit) return false
        if (nameId != other.nameId) return false
        if (symbolId != other.symbolId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + coherentUnit.hashCode()
        result = 31 * result + nameId
        result = 31 * result + symbolId
        return result
    }
}