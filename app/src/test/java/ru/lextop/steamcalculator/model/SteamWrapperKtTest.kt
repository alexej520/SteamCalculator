package ru.lextop.steamcalculator.model

import org.junit.Assert.assertEquals
import org.junit.Test
import steam.quantities.ThermalConductivity
import steam.units.kg_s2

class SteamWrapperKtTest {
    @Test
    fun quantityIdMap_everyQuantityWrapperHasUniqueId() {
        assertEquals(quantityIdMap.size, allQuantities.size)
    }

    @Test
    fun unitIdMap_everyUnitWrapperHasUniqueId() {
        assertEquals(unitIdMap.size, allUnits.size)
    }

    @Test
    fun Quantity_wrapper_QuantityWrapperHasCircularReferenceToQuantity() {
        val quantity = ThermalConductivity
        assertEquals(quantity, quantity.wrapper.quantity)
    }

    @Test
    fun UnitConverter_wrapper_UnitConverterWrapperHasCircularReferenceToUnitConverter() {
        val unitConverter = kg_s2
        assertEquals(unitConverter, unitConverter.wrapper.unit)
    }
}
