package ru.lextop.steamcalculator.steam.quantity

data class DerivedQuantity(val name: String, val symbol: String, val coherentUnit: CoherentUnit, val nameId: Int, val symbolId: Int) {
    init {
        if (nameId != 0 && symbolId != 0) {
            if ((SYMBOL_PROP_MAP as MutableMap).put(symbol, this) != null) {
                throw RuntimeException("DerivedQuantity with same symbol already created: $symbol")
            }
        }
    }

    operator fun invoke(value: Number, unit: DerivedUnit) = QuantityValue(this, value, unit)
    operator fun invoke(value: Double, unit: DerivedUnit) = QuantityValue(this, value, unit)

    companion object {
        val SYMBOL_PROP_MAP: Map<String, DerivedQuantity> = mutableMapOf()
    }
}