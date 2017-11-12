package ru.lextop.steamcalculator.steam.quantity

data class Property(val name: String, val symbol: String, val baseUnit: BaseUnit) {
    operator fun invoke(value: Number, unit: UnitPh) = Quantity(this, value, unit)
    operator fun invoke(value: Double, unit: UnitPh) = Quantity(this, value, unit)
}