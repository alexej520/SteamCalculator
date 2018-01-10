package ru.lextop.steamcalculator.steam.quantity

import ru.lextop.steamcalculator.steam.unit.CoherentUnt

abstract class Quantity(
        val name: String,
        val symbol: String,
        val coherentUnit: CoherentUnt) {

    override fun toString(): String = name
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quantity) return false

        if (name != other.name) return false
        if (symbol != other.symbol) return false
        if (coherentUnit != other.coherentUnit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + coherentUnit.hashCode()
        return result
    }
}