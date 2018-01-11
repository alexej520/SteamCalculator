package ru.lextop.steamcalculator.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomDatabase
import quantityvalue.CoherentUnit
import quantityvalue.invoke
import ru.lextop.steamcalculator.model.*

object OnDbCreateCallback: RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        db.fillTable(
                tableName = EditUnit.TABLE_NAME,
                columns = listOf(
                        EditUnit.PROP_SYMBOL,
                        EditUnit.UNIT_NAME),
                values = computableQuantities.map { prop ->
                    listOf(
                            prop.symbol,
                            defaultUnits[prop.dimension]!!.name)
                })
        db.fillTable(
                tableName = ViewUnit.TABLE_NAME,
                columns = listOf(
                        ViewUnit.PROP_SYMBOL,
                        ViewUnit.UNIT_NAME),
                values = allQuantities.map { prop ->
                    listOf(
                            prop.symbol,
                            defaultUnits[prop.dimension]!!.name)
                })
        val first = computablePropMap.keys.first()
        val second = computablePropMap[first]!!.first()
        db.fillTable(
                tableName = SelectedQuantity.TABLE_NAME,
                columns = listOf(
                        SelectedQuantity.KEY,
                        SelectedQuantity.PROP_SYMBOL,
                        SelectedQuantity.VALUE),
                values = listOf(
                        SelectedQuantity(KEY_FIRST_PROP, first(Double.NaN, CoherentUnit(first.dimension))),
                        SelectedQuantity(KEY_SECOND_PROP, second(Double.NaN, CoherentUnit(second.dimension)))).map {
                    listOf(
                            it.key,
                            it.propSymbol,
                            it.value!!)
                }
        )
    }
}

private fun SupportSQLiteDatabase.fillTable(tableName: String, columns: List<String>, values: List<List<Any>>) {
    val insert = columns.joinToString(
            prefix = "INSERT INTO $tableName (",
            separator = ", ",
            postfix = ") VALUES "
    )
    val sql = values.joinToString(
            prefix = insert,
            separator = ", ",
            postfix = ";",
            transform = { list ->
                list.joinToString(
                        prefix = "('",
                        separator = "', '",
                        postfix = "')"
                )
            }
    )
    execSQL(sql)
}
