package ru.lextop.steamcalculator.steam.quantity

data class Property(val name: String, val symbol: String, val baseUnit: BaseUnit, val nameId: Int, val symbolId: Int) {
    init {
        if (nameId != 0 && symbolId != 0) {
            if ((symbolPropMap as MutableMap).put(symbol, this) != null) {
                throw RuntimeException("Property with same symbol already created: $symbol")
            }
        }
    }

    operator fun invoke(value: Number, unit: UnitPh) = Quantity(this, value, unit)
    operator fun invoke(value: Double, unit: UnitPh) = Quantity(this, value, unit)

    companion object {
        val symbolPropMap: Map<String, Property> = mutableMapOf()
    }
}