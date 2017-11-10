package ru.lextop.steamcalculator.steam.quantity

data class Property(val name: String, val symbol: String, val baseUnit: BaseUnit) {
    operator fun invoke(value: Number, unit: DerivativeUnit) = Quantity(this, value, unit)
    operator fun invoke(value: Double, unit: DerivativeUnit) = Quantity(this, value, unit)
}