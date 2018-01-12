package ru.lextop.steamcalculator.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import ru.lextop.steamcalculator.model.*

@Entity(tableName = ViewUnit.TABLE_NAME)
data class ViewUnit(
        @ColumnInfo(name = QUANTITY_ID)
        @PrimaryKey
        val quantityId: Int,
        @ColumnInfo(name = UNIT_ID)
        val unitId: Int
) {
    companion object {
        const val TABLE_NAME = "viewUnit"
        const val QUANTITY_ID = "quantityId"
        const val UNIT_ID = "unitId"
    }
}

@JvmName("viewUnitToQuantityValuePairs")
fun List<ViewUnit>.toQuantityUnitPairs(): List<Pair<QuantityWrapper, UnitConverterWrapper>> =
        map {
            quantityIdMap[it.quantityId]!! to unitIdMap[it.unitId]!!
        }

@Entity(tableName = EditUnit.TABLE_NAME)
data class EditUnit(
        @ColumnInfo(name = QUANTITY_ID)
        @PrimaryKey
        val quantityId: Int,
        @ColumnInfo(name = UNIT_ID)
        val unitId: Int
) {
    companion object {
        const val TABLE_NAME = "editUnit"
        const val QUANTITY_ID = "quantityId"
        const val UNIT_ID = "unitId"
    }
}

@JvmName("editUnitToQuantityValuePairs")
fun List<EditUnit>.toQuantityUnitPairs(): List<Pair<QuantityWrapper, UnitConverterWrapper>> =
        map {
            quantityIdMap[it.quantityId]!! to unitIdMap[it.unitId]!!
        }

@Entity(tableName = SelectedQuantityValue.TABLE_NAME)
data class SelectedQuantityValue(
        @ColumnInfo(name = ID)
        @PrimaryKey
        val id: Int,
        @ColumnInfo(name = QUANTITY_ID)
        val quantityId: Int,
        @ColumnInfo(name = VALUE)
        val value: Double?
) {
    constructor(id: Int, quantityValue: QuantityValueWrapper)
            : this(id, quantityValue.quantity.id, quantityValue[quantityValue.quantity.defaultUnits.si].value.takeUnless { it.isNaN() })

    companion object {
        const val TABLE_NAME = "selectedQuantityValue"
        const val ID = "id"
        const val QUANTITY_ID = "quantityId"
        const val VALUE = "value"
    }
}

const val ID_ARG1 = 1
const val ID_ARG2 = 2

fun SelectedQuantityValue.toQuantityValue(): QuantityValueWrapper {
    val quantity = quantityIdMap[quantityId]!!
    return QuantityValueWrapper(
            quantity = quantity,
            value = value ?: Double.NaN,
            unit = quantity.defaultUnits.si)
}

@Suppress("UNCHECKED_CAST")
fun List<SelectedQuantityValue>.toQuantityMap(): Map<Int, QuantityValueWrapper> =
        map { it.id to it.toQuantityValue() }.toMap()
