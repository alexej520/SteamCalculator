package ru.lextop.steamcalculator.di

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.defaultSharedPreferences
import ru.lextop.steamcalculator.*
import ru.lextop.steamcalculator.db.*
import ru.lextop.steamcalculator.steam.quantity.Property
import javax.inject.Singleton

@Module(includes = arrayOf(
        ViewModelModule::class
))
abstract class AppModule {
    @Module
    companion object {
        private lateinit var steamDao: SteamDao

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

        @Provides
        @Singleton
        @JvmStatic
        fun provideDatabase(app: Application): AppDatabase = Room
                .databaseBuilder(app, AppDatabase::class.java, "app-db")
                .allowMainThreadQueries()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.fillTable(
                                tableName = EditUnit.TABLE_NAME,
                                columns = listOf(
                                        EditUnit.PROP_SYMBOL,
                                        EditUnit.UNIT_NAME),
                                values = computableProps.map { prop ->
                                    listOf(
                                            prop.symbol,
                                            prop.baseUnit.alias.name)
                                })
                        db.fillTable(
                                tableName = ViewUnit.TABLE_NAME,
                                columns = listOf(
                                        ViewUnit.PROP_SYMBOL,
                                        ViewUnit.UNIT_NAME),
                                values = allProps.map { prop ->
                                    listOf(
                                            prop.symbol,
                                            prop.baseUnit.alias.name)
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
                                        SelectedQuantity(KEY_FIRST_PROP, first(Double.NaN, first.baseUnit.alias)),
                                        SelectedQuantity(KEY_SECOND_PROP, second(Double.NaN, second.baseUnit.alias))).map {
                                    listOf(
                                            it.key,
                                            it.propSymbol,
                                            it.value!!)
                                }
                        )
                    }
                })
                .build()

        @Provides
        @Singleton
        @JvmStatic
        fun provideSharedPreferences(app: Application): SharedPreferences = app.defaultSharedPreferences

        @Provides
        @Singleton
        @JvmStatic
        fun provideApplicationContext(app: Application): Context = app.applicationContext

        @Provides
        @Singleton
        @JvmStatic
        fun provideDerivativeUnitPairDao(db: AppDatabase): SteamDao {
            val steamDao = db.propertyUnitPairDao()
            this.steamDao = steamDao
            return steamDao
        }
    }


}