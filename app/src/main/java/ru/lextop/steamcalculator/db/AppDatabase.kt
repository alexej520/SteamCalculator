package ru.lextop.steamcalculator.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import ru.lextop.steamcalculator.model.*
import steam.quantities.Wavelength
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
                        values = (computableQuantities + Wavelength.wrapper).map { quantity ->
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
                val arg1 = computablePropMap.keys.first()
                val arg2 = computablePropMap[arg1]!!.first()
                val wavelength = Wavelength.wrapper
                db.fillTable(
                        tableName = SelectedQuantityValue.TABLE_NAME,
                        columns = listOf(
                                SelectedQuantityValue.ID,
                                SelectedQuantityValue.QUANTITY_ID,
                                SelectedQuantityValue.VALUE),
                        values = listOf(
                                SelectedQuantityValue(ID_ARG1, QuantityValueWrapper(arg1, Double.NaN, arg1.defaultUnits.default)),
                                SelectedQuantityValue(ID_ARG2, QuantityValueWrapper(arg2, Double.NaN, arg2.defaultUnits.default)),
                                SelectedQuantityValue(ID_WAVELENGTH, QuantityValueWrapper(wavelength, Double.NaN, wavelength.defaultUnits.default))).map {
                            listOf<Number?>(
                                    it.id,
                                    it.quantityId,
                                    it.value)
                        }
                )
            }
        }

        private fun SupportSQLiteDatabase.fillTable(tableName: String, columns: List<String>, values: List<List<Number?>>) {
            val insert = columns.joinToString(
                    prefix = "INSERT INTO `$tableName` (`",
                    separator = "`, `",
                    postfix = "`) VALUES "
            )
            val sql = values.joinToString(
                    prefix = insert,
                    separator = ", ",
                    postfix = ";",
                    transform = { list ->
                        list.joinToString(
                                prefix = "(",
                                separator = ", ",
                                postfix = ")"
                        )
                    }
            )
            execSQL(sql)
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `view_unit`")
                db.execSQL("DROP TABLE IF EXISTS `edit_unit`")
                db.execSQL("DROP TABLE IF EXISTS `selected_property`")
                db.execSQL("CREATE TABLE IF NOT EXISTS `viewUnit` (`quantityId` INTEGER NOT NULL, `unitId` INTEGER NOT NULL, PRIMARY KEY(`quantityId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `editUnit` (`quantityId` INTEGER NOT NULL, `unitId` INTEGER NOT NULL, PRIMARY KEY(`quantityId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `selectedQuantityValue` (`id` INTEGER NOT NULL, `quantityId` INTEGER NOT NULL, `value` REAL, PRIMARY KEY(`id`))")
                db.fillTable(
                        tableName = EditUnit.TABLE_NAME,
                        columns = listOf(
                                EditUnit.QUANTITY_ID,
                                EditUnit.UNIT_ID),
                        values = (computableQuantities + Wavelength.wrapper).map { quantity ->
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
                val arg1 = computablePropMap.keys.first()
                val arg2 = computablePropMap[arg1]!!.first()
                val wavelength = Wavelength.wrapper
                db.fillTable(
                        tableName = SelectedQuantityValue.TABLE_NAME,
                        columns = listOf(
                                SelectedQuantityValue.ID,
                                SelectedQuantityValue.QUANTITY_ID,
                                SelectedQuantityValue.VALUE),
                        values = listOf(
                                SelectedQuantityValue(ID_ARG1, QuantityValueWrapper(arg1, Double.NaN, arg1.defaultUnits.default)),
                                SelectedQuantityValue(ID_ARG2, QuantityValueWrapper(arg2, Double.NaN, arg2.defaultUnits.default)),
                                SelectedQuantityValue(ID_WAVELENGTH, QuantityValueWrapper(wavelength, Double.NaN, wavelength.defaultUnits.default))).map {
                            listOf<Number?>(
                                    it.id,
                                    it.quantityId,
                                    it.value)
                        }
                )
            }
        }
    }
}
