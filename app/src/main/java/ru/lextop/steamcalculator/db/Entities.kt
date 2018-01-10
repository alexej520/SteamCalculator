package ru.lextop.steamcalculator.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import ru.lextop.steamcalculator.steam.quantity.DerivedUnit
import ru.lextop.steamcalculator.steam.quantity.DerivedQuantity
import ru.lextop.steamcalculator.steam.quantity.QuantityValue
import ru.lextop.steamcalculator.toQuantity
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
fun List<ViewUnit>.toPropUnitList(): List<Pair<DerivedQuantity, DerivedUnit>> =
        map {
            val propType = it.propSymbol.toQuantity()
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
fun List<EditUnit>.toPropUnitList(): List<Pair<DerivedQuantity, DerivedUnit>> =
        map {
            val propType = it.propSymbol.toQuantity()
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
    constructor(key: String, quantityValue: QuantityValue)
            : this(key, quantityValue.quantity.symbol, quantityValue[quantityValue.quantity.coherentUnit.derived].value)

    companion object {
        const val TABLE_NAME = "selected_property"
        const val KEY = "key"
        const val PROP_SYMBOL = "propSymbol"
        const val VALUE = "value"
    }
}

const val KEY_FIRST_PROP = "first"
const val KEY_SECOND_PROP = "second"

fun SelectedQuantity.toQuantity(): QuantityValue {
    val prop = propSymbol.toQuantity()
    return prop(value ?: Double.NaN, prop.coherentUnit.derived)
}

@Suppress("UNCHECKED_CAST")
fun List<SelectedQuantity>.toQuantityMap(): Map<String, QuantityValue> =
        map { it.key to it.toQuantity() }.toMap()
