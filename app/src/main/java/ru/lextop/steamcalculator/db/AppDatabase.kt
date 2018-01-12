package ru.lextop.steamcalculator.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import ru.lextop.steamcalculator.model.QuantityValueWrapper
import ru.lextop.steamcalculator.model.allQuantities
import ru.lextop.steamcalculator.model.computablePropMap
import ru.lextop.steamcalculator.model.computableQuantities
import javax.inject.Singleton

@Singleton
@Database(entities = arrayOf(
        ViewUnit::class,
        EditUnit::class,
        SelectedQuantityValue::class
), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun steamDao(): SteamDao

    companion object {
        val ON_CREATE = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.fillTable(
                        tableName = EditUnit.TABLE_NAME,
                        columns = listOf(
                                EditUnit.QUANTITY_ID,
                                EditUnit.UNIT_ID),
                        values = computableQuantities.map { quantity ->
                            listOf(
                                    quantity.id,
                                    quantity.defaultUnits.default.id)
                        })
                db.fillTable(
                        tableName = ViewUnit.TABLE_NAME,
                        columns = listOf(
                                ViewUnit.QUANTITY_ID,
                                ViewUnit.UNIT_ID),
                        values = allQuantities.map { quantity ->
                            listOf(
                                    quantity.id,
                                    quantity.defaultUnits.default.id)
                        })
                val first = computablePropMap.keys.first()
                val second = computablePropMap[first]!!.first()
                db.fillTable(
                        tableName = SelectedQuantityValue.TABLE_NAME,
                        columns = listOf(
                                SelectedQuantityValue.ID,
                                SelectedQuantityValue.QUANTITY_ID,
                                SelectedQuantityValue.VALUE),
                        values = listOf(
                                SelectedQuantityValue(ID_ARG1, QuantityValueWrapper(first, Double.NaN, first.defaultUnits.default)),
                                SelectedQuantityValue(ID_ARG2, QuantityValueWrapper(second, Double.NaN, second.defaultUnits.default))).map {
                            listOf(
                                    it.id,
                                    it.quantityId,
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
                                prefix = "(`",
                                separator = "`, `",
                                postfix = "`)"
                        )
                    }
            )
            execSQL(sql)
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    begin;
                    DROP TABLE IF EXISTS `view_unit`;
                    DROP TABLE IF EXISTS `edit_unit`;
                    DROP TABLE IF EXISTS `selected_property`;
                    commit;
                    """.trimIndent())
                db.execSQL("CREATE TABLE IF NOT EXISTS `viewUnit` (`quantityId` INTEGER NOT NULL, `unitId` INTEGER NOT NULL, PRIMARY KEY(`quantityId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `editUnit` (`quantityId` INTEGER NOT NULL, `unitId` INTEGER NOT NULL, PRIMARY KEY(`quantityId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `selectedQuantityValue` (`id` INTEGER NOT NULL, `quantityId` INTEGER NOT NULL, `value` REAL, PRIMARY KEY(`id`))")
                db.fillTable(
                        tableName = EditUnit.TABLE_NAME,
                        columns = listOf(
                                EditUnit.QUANTITY_ID,
                                EditUnit.UNIT_ID),
                        values = computableQuantities.map { quantity ->
                            listOf(
                                    quantity.id,
                                    quantity.defaultUnits.default.id)
                        })
                db.fillTable(
                        tableName = ViewUnit.TABLE_NAME,
                        columns = listOf(
                                ViewUnit.QUANTITY_ID,
                                ViewUnit.UNIT_ID),
                        values = allQuantities.map { quantity ->
                            listOf(
                                    quantity.id,
                                    quantity.defaultUnits.default.id)
                        })
                val first = computablePropMap.keys.first()
                val second = computablePropMap[first]!!.first()
                db.fillTable(
                        tableName = SelectedQuantityValue.TABLE_NAME,
                        columns = listOf(
                                SelectedQuantityValue.ID,
                                SelectedQuantityValue.QUANTITY_ID,
                                SelectedQuantityValue.VALUE),
                        values = listOf(
                                SelectedQuantityValue(ID_ARG1, QuantityValueWrapper(first, Double.NaN, first.defaultUnits.default)),
                                SelectedQuantityValue(ID_ARG2, QuantityValueWrapper(second, Double.NaN, first.defaultUnits.default))).map {
                            listOf(
                                    it.id,
                                    it.quantityId,
                                    it.value!!)
                        }
                )
            }
        }
    }
}
