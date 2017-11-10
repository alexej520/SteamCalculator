package ru.lextop.steamcalculator.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import ru.lextop.steamcalculator.steam.quantity.DerivativeUnit
import ru.lextop.steamcalculator.steam.quantity.Property
import ru.lextop.steamcalculator.steam.quantity.Quantity
import ru.lextop.steamcalculator.toProperty
import ru.lextop.steamcalculator.toUnit

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

@JvmName("viewUnitToUnitMap")
fun List<ViewUnit>.toPropUnitList(): List<Pair<Property, DerivativeUnit>> =
        map {
            val propType = it.propSymbol.toProperty()
            propType to it.unitName.toUnit(propType)
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

@JvmName("editUnitToUnitMap")
fun List<EditUnit>.toPropUnitList(): List<Pair<Property, DerivativeUnit>> =
        map {
            val propType = it.propSymbol.toProperty()
            propType to it.unitName.toUnit(propType)
        }

@Entity(tableName = SelectedQuantity.TABLE_NAME)
data class SelectedQuantity(
        @ColumnInfo(name = KEY)
        @PrimaryKey
        val key: String,
        @ColumnInfo(name = PROP_SYMBOL)
        val propSymbol: String,
        @ColumnInfo(name = VALUE)
        val value: Double?
) {
    constructor(key: String, quantity: Quantity)
            : this(key, quantity.property.symbol, quantity[quantity.property.baseUnit.alias].value)

    companion object {
        const val TABLE_NAME = "selected_property"
        const val KEY = "key"
        const val PROP_SYMBOL = "propSymbol"
        const val VALUE = "value"
    }
}

const val KEY_FIRST_PROP = "first"
const val KEY_SECOND_PROP = "second"

fun SelectedQuantity.toQuantity(): Quantity {
    val prop = propSymbol.toProperty()
    return prop(value ?: Double.NaN, prop.baseUnit.alias)
}

@Suppress("UNCHECKED_CAST")
fun List<SelectedQuantity>.toQuantityMap(): Map<String, Quantity> =
        map { it.key to it.toQuantity() }.toMap()
