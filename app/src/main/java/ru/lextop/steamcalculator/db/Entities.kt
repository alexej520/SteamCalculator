package ru.lextop.steamcalculator.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import quantityvalue.*
import ru.lextop.steamcalculator.model.*

@Entity(tableName = ViewUnit.TABLE_NAME)
data class ViewUnit(
        @ColumnInfo(name = PROP_SYMBOL)
        @PrimaryKey
        val propSymbol: String,
        @ColumnInfo(name = UNIT_NAME)
        val unitName: String
) {
    companion object {
        const val TABLE_NAME = "view_unit"
        const val PROP_SYMBOL = "prop_symbol"
        const val UNIT_NAME = "unit_name"
    }
}

@JvmName("viewUnitToQuantityValuePairs")
fun List<ViewUnit>.toQuantityUnitPairs(): List<Pair<Quantity, UnitPh>> =
        map {
            val propType = it.propSymbol.toQuantity()
            propType to it.unitName.toUnit()
        }

@Entity(tableName = EditUnit.TABLE_NAME)
data class EditUnit(
        @ColumnInfo(name = PROP_SYMBOL)
        @PrimaryKey
        val propSymbol: String,
        @ColumnInfo(name = UNIT_NAME)
        val unitName: String
) {
    companion object {
        const val TABLE_NAME = "edit_unit"
        const val PROP_SYMBOL = "prop_symbol"
        const val UNIT_NAME = "unit_name"
    }
}

@JvmName("editUnitToQuantityValuePairs")
fun List<EditUnit>.toQuantityUnitPairs(): List<Pair<Quantity, UnitPh>> =
        map {
            val propType = it.propSymbol.toQuantity()
            propType to it.unitName.toUnit()
        }

@Entity(tableName = SelectedQuantityValue.TABLE_NAME)
data class SelectedQuantityValue(
        @ColumnInfo(name = KEY)
        @PrimaryKey
        val key: String,
        @ColumnInfo(name = PROP_SYMBOL)
        val propSymbol: String,
        @ColumnInfo(name = VALUE)
        val value: Double?
) {
    constructor(key: String, quantityValue: QuantityValue)
            : this(key, quantityValue.quantity.symbol, quantityValue[siUnits[quantityValue.unit.dimension]!!].value)

    companion object {
        const val TABLE_NAME = "selected_property"
        const val KEY = "key"
        const val PROP_SYMBOL = "quantitySymbol"
        const val VALUE = "value"
    }
}

const val KEY_FIRST_QUANTITY = "first"
const val KEY_SECOND_QUANTITY = "second"

fun SelectedQuantityValue.toQuantityValue(): QuantityValue {
    val prop = propSymbol.toQuantity()
    return prop(value ?: Double.NaN, siUnits[prop.dimension]!!)
}

@Suppress("UNCHECKED_CAST")
fun List<SelectedQuantityValue>.toQuantityMap(): Map<String, QuantityValue> =
        map { it.key to it.toQuantityValue() }.toMap()
