package ru.lextop.steamcalculator.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import javax.inject.Singleton

@Singleton
@Database(entities = arrayOf(
        ViewUnit::class,
        EditUnit::class,
        SelectedQuantity::class
), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun propertyUnitPairDao(): SteamDao
}