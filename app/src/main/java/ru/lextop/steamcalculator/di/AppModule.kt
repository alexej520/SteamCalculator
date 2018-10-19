package ru.lextop.steamcalculator.di

import android.app.Application
import androidx.room.Room
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import ru.lextop.steamcalculator.db.AppDatabase
import ru.lextop.steamcalculator.db.SteamDao
import javax.inject.Singleton

@Module(includes = arrayOf(
        ViewModelModule::class
))
abstract class AppModule {
    @Module
    companion object {
        private lateinit var steamDao: SteamDao

        @Provides
        @Singleton
        @JvmStatic
        fun provideDatabase(app: Application): AppDatabase = Room
                .databaseBuilder(app, AppDatabase::class.java, "app-db")
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabase.ON_CREATE)
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()

        @Provides
        @Singleton
        @JvmStatic
        fun provideSharedPreferences(app: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

        @Provides
        @Singleton
        @JvmStatic
        fun provideApplicationContext(app: Application): Context = app.applicationContext

        @Provides
        @Singleton
        @JvmStatic
        fun provideSteamDao(db: AppDatabase): SteamDao {
            val steamDao = db.steamDao()
            this.steamDao = steamDao
            return steamDao
        }
    }


}